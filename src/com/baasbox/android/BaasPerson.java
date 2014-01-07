package com.baasbox.android;

import com.baasbox.android.json.JsonConvertible;
import com.baasbox.android.json.JsonObject;

/**
 * Created by eto on 02/01/14.
 */
public class BaasPerson implements JsonConvertible {
    public final String username;

    protected final JsonObject privateData;
    protected final JsonObject friendVisibleData;
    protected final JsonObject registeredVisibleData;
    protected final JsonObject publicVisibleData;

    public enum Scope {
        PRIVATE("visibleByTheUser"),
        FRIEND("visibleByFriends"),
        REGISTERED("visibleByRegisteredUsers"),
        PUBLIC("visibleByAnonymousUsers");

        final String visibilityName;

        Scope(String visibilityName) {
            this.visibilityName = visibilityName;
        }
    }

    protected BaasPerson(String username, JsonObject data) {
        this.username = username;
        this.privateData = data.getObject(Scope.PRIVATE.visibilityName, new JsonObject());
        this.friendVisibleData = data.getObject(Scope.FRIEND.visibilityName, new JsonObject());
        this.registeredVisibleData = data.getObject(Scope.REGISTERED.visibilityName, new JsonObject());
        this.publicVisibleData = data.getObject(Scope.PUBLIC.visibilityName, new JsonObject());
    }

    protected BaasPerson(String username) {
        this.username = username;
        this.privateData = new JsonObject();
        this.friendVisibleData = new JsonObject();
        this.registeredVisibleData = new JsonObject();
        this.publicVisibleData = new JsonObject();
    }

    public JsonObject getScope(Scope scope) {
        switch (scope) {
            case PRIVATE:
                return privateData;
            case FRIEND:
                return friendVisibleData;
            case REGISTERED:
                return registeredVisibleData;
            case PUBLIC:
                return publicVisibleData;
            default:
                throw new NullPointerException("scope cannot be null");
        }
    }

    protected JsonObject toJson(boolean credentials) {
        JsonObject object = new JsonObject();
        if (credentials) {
            object.putString("username", username);
        }
        object.putObject(Scope.PRIVATE.visibilityName, privateData)
                .putObject(Scope.FRIEND.visibilityName, friendVisibleData)
                .putObject(Scope.REGISTERED.visibilityName, registeredVisibleData)
                .putObject(Scope.PUBLIC.visibilityName, publicVisibleData);
        return object;
    }

    @Override
    public JsonObject toJson() {
        return toJson(true);
    }

}
