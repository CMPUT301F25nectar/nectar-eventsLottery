
package com.example.beethere;

public final class Session {
    private static User current; // null until saved
    private Session(){}

    public static void save(User u) { current = u; }
    public static User get() { return current; }
    public static void clear() { current = null; }

    // basic “profile exists” check for gating Join/Create
    public static boolean hasProfile() {
        return current != null
                && current.getEmail() != null
                && !current.getEmail().trim().isEmpty();
    }
}

