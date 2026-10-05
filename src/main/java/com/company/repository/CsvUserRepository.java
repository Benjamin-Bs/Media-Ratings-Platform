package com.company.repository;


import com.company.models.User;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

public class CsvUserRepository implements UserRepository {

    private final String databaseLocation;

    public CsvUserRepository(String databaseLocation) throws IOException {
        this.databaseLocation = databaseLocation;
        File file = new File(databaseLocation);

        if (!file.exists()) {
            File parentDir = file.getParentFile();
            if (parentDir != null) {
                if (!parentDir.exists()) {
                    parentDir.mkdirs();
                }
            }
            file.createNewFile();
        }
    }

    private String userToCsvLine(User user) {
        String username = user.getUsername();
        if (username == null) {
            username = "";
        }

        String password = user.getPassword();
        if (password == null) {
            password = "";
        }

        String token = user.getToken();
        if (token == null) {
            token = "";
        }

        String favs = "";
        if (user.getFavMediaIds() != null) {
            if (!user.getFavMediaIds().isEmpty()) {
                StringBuilder sb = new StringBuilder();
                for (int i = 0; i < user.getFavMediaIds().size(); i++) {
                    sb.append(user.getFavMediaIds().get(i));
                    if (i < user.getFavMediaIds().size() - 1) {
                        sb.append(";");
                    }
                }
                favs = sb.toString();
            }
        }

        return user.getId() + "," + username + "," + password + "," + token + "," + favs + "\n";
    }

    private User csvToUser(String line) {
        if (line == null) {
            return null;
        }
        if (line.trim().isEmpty()) {
            return null;
        }

        String[] columns = line.split(",", -1);
        if (columns.length < 3) {
            return null;
        }

        int id = Integer.parseInt(columns[0]);
        String username = columns[1];
        String password = columns[2];

        User user = new User(id, username, password);

        if (columns.length > 3) {
            if (!columns[3].isEmpty()) {
                user.setToken(columns[3]);
            }
        }

        if (columns.length > 4) {
            if (!columns[4].isEmpty()) {
                String[] favArray = columns[4].split(";");
                List<Integer> favList = new ArrayList<>();
                for (String favIdStr : favArray) {
                    favList.add(Integer.parseInt(favIdStr));
                }
                user.setFavMediaIds(favList);
            }
        }

        return user;
    }

    private void flush(List<User> users) {
        StringBuilder sb = new StringBuilder();
        for (User user : users) {
            sb.append(userToCsvLine(user));
        }

        try {
            Files.writeString(Path.of(databaseLocation), sb.toString());
        } catch (IOException e) {
            throw new RuntimeException("Fehler beim Speichern der Datei", e);
        }
    }

    public List<User> findAll() {
        List<User> users = new ArrayList<>();
        try {
            List<String> lines = Files.readAllLines(Path.of(databaseLocation));
            for (String line : lines) {
                User user = csvToUser(line);
                if (user != null) {
                    users.add(user);
                }
            }
        } catch (IOException e) {
            throw new RuntimeException("Fehler beim Lesen der Datei", e);
        }
        return users;
    }

    @Override
    public int createUser(User user) {
        List<User> users = findAll();

        int maxId = 0;
        for (User u : users) {
            if (u.getId() > maxId) {
                maxId = u.getId();
            }
        }

        int newId = maxId + 1;
        user.setId(newId);
        users.add(user);

        flush(users);
        return newId;
    }

    @Override
    public User getUser(int userId) {
        List<User> users = findAll();
        for (User user : users) {
            if (user.getId() == userId) {
                return user;
            }
        }
        return null;
    }

    @Override
    public User getUserByUsername(String username) {
        if (username == null) {
            return null;
        }

        List<User> users = findAll();
        for (User user : users) {
            if (username.equalsIgnoreCase(user.getUsername())) {
                return user;
            }
        }
        return null;
    }

    @Override
    public User getUserByToken(String token) {
        if (token == null) {
            return null;
        }

        List<User> users = findAll();
        for (User user : users) {
            if (token.equals(user.getToken())) {
                return user;
            }
        }
        return null;
    }

    @Override
    public User updateUser(User user) {
        List<User> users = findAll();
        for (int i = 0; i < users.size(); i++) {
            if (users.get(i).getId() == user.getId()) {
                users.set(i, user);
                flush(users);
                return user;
            }
        }
        return null;
    }

    @Override
    public void deleteUser(int userId) {
        List<User> users = findAll();
        boolean found = false;

        for (int i = 0; i < users.size(); i++) {
            if (users.get(i).getId() == userId) {
                users.remove(i);
                found = true;
                break;
            }
        }

        if (found) {
            flush(users);
        }
    }
}
