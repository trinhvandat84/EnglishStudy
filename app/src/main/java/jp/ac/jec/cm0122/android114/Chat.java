package jp.ac.jec.cm0122.android114;

import androidx.annotation.NonNull;

import java.util.Objects;

public final class Chat {
    private final int internalId;
    @NonNull
    private String message;
    private boolean isUser;
    private String avatar;

    public Chat(int internalId, @NonNull String message, boolean isUser, String avatar) {
        this.internalId = internalId;
        this.message = message;
        this.isUser = isUser;
        this.avatar = avatar;
    }

    public int getInternalId() {
        return internalId;
    }

    @NonNull
    public String getMessage() {
        return message;
    }

    public void setMessage(@NonNull String message) {
        this.message = message;
    }

    public boolean isUser() {
        return isUser;
    }

    public void setUser(boolean user) {
        isUser = user;
    }

    public String getAvatar() {
        return avatar;
    }

    public void setAvatar(String avatar) {
        this.avatar = avatar;
    }

    @Override
    public String toString() {
        return "Chat{" +
                "internalId=" + internalId +
                ", message='" + message + '\'' +
                ", isUser=" + isUser +
                ", avatar='" + avatar + '\'' +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Chat chat = (Chat) o;
        return internalId == chat.internalId && isUser == chat.isUser && Objects.equals(message, chat.message) && Objects.equals(avatar, chat.avatar);
    }

    @Override
    public int hashCode() {
        return Objects.hash(internalId, message, isUser, avatar);
    }
}
