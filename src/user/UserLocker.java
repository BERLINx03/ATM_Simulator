package user;

public class UserLocker {

    public int userId;

    public UserLocker(int userId) {
        this.userId = userId;
    }

    void setUserId(int userId) {
        this.userId = userId;
    }

    int getUserId() {
        return userId;
    }
    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        UserLocker userLocker = (UserLocker) obj;
        return userId == userLocker.userId;
    }
    @Override
    public int hashCode() {
        return Integer.hashCode(userId);
    }
}