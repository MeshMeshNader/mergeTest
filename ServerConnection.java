/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package tictactoeserver;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.net.Socket;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.io.InputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.sql.SQLException;
import java.util.ArrayList;
import tictactoeclient.GameDTO;
import tictactoeclient.Messages;
import tictactoeclient.MoveDTO;
import tictactoeclient.UserDTO;

/**
 *
 * @author dell
 */
public class ServerConnection {

    InputStream inputstream;
    OutputStream outputstream;
    ObjectInputStream objectinputstream;
    ObjectOutputStream objectoutputstream;
    Socket socket;
    String ip;
    int portNum;
    Object obj;
    String msg;

    public ServerConnection(Socket socket) {
        try {
            this.socket = socket;
            ip = socket.getInetAddress().getHostAddress();
            portNum = socket.getPort();
            inputstream = socket.getInputStream();
            outputstream = socket.getOutputStream();
            objectinputstream = new ObjectInputStream(inputstream);
            objectoutputstream = new ObjectOutputStream(outputstream);

            DataAccessLayer.connect();
            readMessage();

        } catch (IOException ex) {
            Logger.getLogger(ServerConnection.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public String getIp() {
        return ip;
    }

    public void readMessage() {
        new Thread() {
            @Override
            public void run() {

                while (!socket.isClosed() && socket.isConnected()) {
                    try {
                        msg = (String) objectinputstream.readObject();

                        if (msg.equals(Messages.loginRequest)) {

                            obj = (UserDTO) objectinputstream.readObject();
                            sendMessage(Messages.loginResponse, loginValidation());

                        } else if (msg.equals(Messages.registrationRequest)) {

                            obj = (UserDTO) objectinputstream.readObject();
                            sendMessage(Messages.registrationResponse, registrationValidation());

                        } else if (msg.equals(Messages.getOnlineUsersRequest)) {
                            sendMessage(Messages.getOnlineUsersResponse, retriveOnlineUsers());

                        } else if (msg.equals(Messages.incNumOfWinsRequest)) {
                            sendMessage(Messages.incNumOfWinsResponse, incNumOfWins());

                        } else if (msg.equals(Messages.incNumOflossesRequest)) {
                            sendMessage(Messages.incNumOflossesResponse, incNumOfLossess());

                        } else if (msg.equals(Messages.setGameRequest)) {
                            obj = (GameDTO) objectinputstream.readObject();
                            sendMessage(Messages.setGameResponse, setGameResponse());

                        } else if (msg.equals(Messages.getGameRequest)) {
                            sendMessage(Messages.getGameResponse, retriveGame());

                        } else if (msg.equals(Messages.deleteGameRequest)) {
                            sendMessage(Messages.deleteGameResponse, gameDeleted());

                        } else if (msg.equals(Messages.deleteMoveRequest)) {
                            sendMessage(Messages.deleteMoveResponse, moveDeleted());

                        } else if (msg.equals(Messages.makeUserOnlineRequest)) {
                            sendMessage(Messages.makeUserOnlineResponse, makeUserOnline());

                        } else if (msg.equals(Messages.makeUserOfflineRequest)) {
                            sendMessage(Messages.makeUserOfflineResponse, makeuserOffline());

                        } else if (msg.equals(Messages.getNumberOfWinsRequest)) {
                            sendMessage(Messages.getNumberOfWinsResponse, getNumberOfWins());

                        } else if (msg.equals(Messages.getNumberOfLossessRequest)) {
                            sendMessage(Messages.getNumberOfLossesResponse, getNumberOfLossess());
                        }
                        

                    } catch (IOException ex) {
                        Logger.getLogger(ServerConnection.class.getName()).log(Level.SEVERE, null, ex);
                    } catch (ClassNotFoundException ex) {
                        Logger.getLogger(ServerConnection.class.getName()).log(Level.SEVERE, null, ex);
                    } catch (SQLException ex) {
                        Logger.getLogger(ServerConnection.class.getName()).log(Level.SEVERE, null, ex);
                    }

                }
            }

        }.start();
    }

    public void sendMessage(String msg, Object obj) {
        new Thread() {
            @Override
            public void run() {

                try {
                    objectoutputstream.writeObject(msg);
                    objectoutputstream.writeObject(obj);
                } catch (IOException ex) {
                    Logger.getLogger(ServerConnection.class.getName()).log(Level.SEVERE, null, ex);
                }

            }
        }.start();
    }

    public ArrayList<UserDTO> retriveOnlineUsers() throws SQLException {
        ArrayList<UserDTO> usersOnline;
        usersOnline = DataAccessLayer.getOnlinePlayers();
        if (usersOnline != null) {
            return usersOnline;
        } else {
            return (new ArrayList<UserDTO>());
        }

    }

    public GameDTO retriveGame() throws SQLException {
        GameDTO Game;
        Game = DataAccessLayer.getGame(((GameDTO) obj).getGameID());
        if (Game != null) {
            return Game;
        } else {
            return (new GameDTO());
        }

    }

    public Boolean incNumOfWins() {
        Boolean result = DataAccessLayer.incrementNumberOfWins(((UserDTO) obj).getUserName());

        return result;

    }

    public Boolean incNumOfLossess() {
        Boolean result = DataAccessLayer.incrementNumberOfLosses(((UserDTO) obj).getUserName());
        return result;
    }

    public Boolean setGameResponse() {
        Boolean result = DataAccessLayer.setGame((GameDTO) obj);
        return result;
    }

    public Boolean gameDeleted() throws SQLException {
        Boolean result = DataAccessLayer.deleteGame(((GameDTO) obj).getGameID());
        return result;
    }

    public Boolean moveDeleted() throws SQLException {
        int result = DataAccessLayer.deleteMove(((MoveDTO) obj).getMoveID());
        if (result == 1) {
            return true;
        } else {
            return false;
        }
    }

    public Boolean makeUserOnline() {
        Boolean result = DataAccessLayer.makeuserOnline(((UserDTO) obj).getUserName());
        return result;

    }

    public Boolean makeuserOffline() {
        Boolean result = DataAccessLayer.makeuserOffline(((UserDTO) obj).getUserName());
        return result;

    }

    public Integer getNumberOfWins() {
        Integer wins;
        wins = DataAccessLayer.getNumberOfWins(((UserDTO) obj).getUserName());
        return wins;
    }

    public Integer getNumberOfLossess() {
        Integer losses;
        losses = DataAccessLayer.getNumberOfLosses(((UserDTO) obj).getUserName());
        return losses;
    }

}
