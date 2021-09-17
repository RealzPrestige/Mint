package mint.commands;

import com.mojang.realmsclient.gui.ChatFormatting;
import mint.Mint;
import mint.managers.FriendManager;

public class Friend
        extends Command {
    public Friend() {
        super("friend", new String[]{"add/del/name/clear", "name"});
    }

    @Override
    public void execute(String[] commands) {
        if (commands.length == 1) {
            if (Mint.friendManager.getFriends().isEmpty()) {
                Friend.sendMessage("Friend list empty D:.");
            } else {
                String f = "Friends: ";
                for (FriendManager.Friend friend : Mint.friendManager.getFriends()) {
                    try {
                        f = f + friend.getUsername() + ", ";
                    } catch (Exception exception) {
                    }
                }
                Friend.sendMessage(f);
            }
            return;
        }
        if (commands.length == 2) {
            switch (commands[0]) {
                case "reset": {
                    Mint.friendManager.onLoad();
                    Friend.sendMessage("Friends got reset.");
                    return;
                }
            }
            Friend.sendMessage(commands[0] + (Mint.friendManager.isFriend(commands[0]) ? " is friended." : " isn't friended."));
            return;
        }
        if (commands.length >= 2) {
            switch (commands[0]) {
                case "add": {
                    Mint.friendManager.addFriend(commands[1]);
                    Friend.sendMessage(ChatFormatting.WHITE + commands[1] + " has been friended");
                    return;
                }
                case "del": {
                    Mint.friendManager.removeFriend(commands[1]);
                    Friend.sendMessage(ChatFormatting.WHITE + commands[1] + " has been unfriended");
                    return;
                }
            }
            Friend.sendMessage("Unknown Command, try friend add/del (name)");
        }
    }
}

