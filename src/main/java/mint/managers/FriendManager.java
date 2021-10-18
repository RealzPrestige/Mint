package mint.managers;

import mint.modules.Feature;
import mint.clickgui.setting.Setting;
import mint.utils.PlayerUtil;
import net.minecraft.entity.player.EntityPlayer;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

public class FriendManager extends Feature {
    private List<Friend> friends = new ArrayList<>();

    public FriendManager() {
        super("Friends");
    }

    public boolean isFriend(String name) {
        cleanFriends();
        return friends.stream().anyMatch(friend -> friend.username.equalsIgnoreCase(name));
    }

    public boolean isFriend(EntityPlayer player) {
        return isFriend(player.getName());
    }

    public void addFriend(String name) {
        Friend friend = getFriendByName(name);
        if (friend != null) {
            friends.add(friend);
        }
        cleanFriends();
    }

    public void removeFriend(String name) {
        cleanFriends();
        for (Friend friend : friends) {
            if (!friend.getUsername().equalsIgnoreCase(name)) continue;
            friends.remove(friend);
            break;
        }
    }

    public void onLoad() {
        friends = new ArrayList<>();
        clearSettings();
    }

    public void saveFriends() {
        clearSettings();
        cleanFriends();
        for (Friend friend : friends) {
            register(new Setting<>(friend.getUuid().toString(), friend.getUsername()));
        }
    }

    public void cleanFriends() {
        friends.stream().filter(Objects::nonNull).filter(friend -> friend.getUsername() != null);
    }

    public List<Friend> getFriends() {
        cleanFriends();
        return friends;
    }

    public Friend getFriendByName(String input) {
        UUID uuid = PlayerUtil.getUUIDFromName(input);
        if (uuid != null) {
            return new Friend(input, uuid);
        }
        return null;
    }

    public void addFriend(Friend friend) {
        friends.add(friend);
    }

    public static class Friend {
        private final String username;
        private final UUID uuid;

        public Friend(String username, UUID uuid) {
            this.username = username;
            this.uuid = uuid;
        }

        public String getUsername() {
            return username;
        }

        public UUID getUuid() {
            return uuid;
        }
    }
}

