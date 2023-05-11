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
import tictactoeclient.Messages;
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
    UserDTO obj;
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

                        } else if (msg.equals(Messages.getOfflinePlayersNumRequest)) {

                            obj = (UserDTO) objectinputstream.readObject();
                            sendMessage(Messages.getOfflinePlayersNumResponse, getOfflinePlayersNumValidation());

                        } else if (msg.equals(Messages.logoutRequest)) {

                            obj = (UserDTO) objectinputstream.readObject();
                            sendMessage(Messages.logoutResponse, logoutValidation(obj.getUserName()));

                        } else if (msg.equals(Messages.makePlayerOneBusyRequest)) {

                            obj = (UserDTO) objectinputstream.readObject();
                            sendMessage(Messages.makePlayerOneBusyResponse, makePlayerOneBusyValidation(obj.getUserName()));

                        } else if (msg.equals(Messages.makePlayerTwoBusyRequest)) {

                            obj = (UserDTO) objectinputstream.readObject();
                            sendMessage(Messages.makePlayerTwoBusyResponse, makePlayerTwoBusyValidation(obj.getUserName()));

                        }else if (msg.equals(Messages.getPlayerScoreRequest)) {

                            obj = (UserDTO) objectinputstream.readObject();
                            sendMessage(Messages.getPlayerScoreResponse, getPlayerScoreValidation(obj.getUserName()));

                        }else if (msg.equals(Messages.getPlayerScoreRequest)) {

                            obj = (UserDTO) objectinputstream.readObject();
                            sendMessage(Messages.getPlayerScoreResponse, getPlayerScoreValidation(obj.getUserName()));

                        }else if (msg.equals(Messages.updateScoreRequest)) {

                            obj = (UserDTO) objectinputstream.readObject();
                            sendMessage(Messages.updateScoreResponse, updateScoreValidation(obj));

                        }
                       

                    } catch (ClassNotFoundException | IOException e) {
                        e.printStackTrace();
                    }
                }

            }
        }
                .start();

    }

    public void sendMessage(String msg, Object obj) {
        new Thread() {
            @Override
            public void run() {

                try {
                    objectoutputstream.writeObject(msg);
                    objectoutputstream.writeObject(obj);

                } catch (IOException ex) {
                    Logger.getLogger(ServerConnection.class
                            .getName()).log(Level.SEVERE, null, ex);
                }

            }
        }.start();
    }

    public Boolean loginValidation() {
        // check database and return true if exist and true and false if failed
        System.out.println("Login Validation Test");
        /*  if (DataAccessLayer.login(obj.getUserName(), obj.getPassword())) {
            return true;
        } else {
            return false;
        }*/
        return true;
    }

    public boolean registrationValidation() {
        // insert in database and return true if sucess and false if failed
        System.out.println("Login Validation Test");
        return false;
    }

    public Integer getOfflinePlayersNumValidation() {

        System.out.println("OfflinePlayersNum Validation Test");

        try {
            return DataAccessLayer.getOfflinePlayersNum();

        } catch (SQLException ex) {
            Logger.getLogger(ServerConnection.class
                    .getName()).log(Level.SEVERE, null, ex);
        }
        return 0;
    }

    public Boolean logoutValidation(String userName) {

        return DataAccessLayer.logout(userName);
    }

    public Boolean makePlayerOneBusyValidation(String UserName) {
        return DataAccessLayer.makePlayerOneBusy(UserName);
    }

    public Boolean makePlayerTwoBusyValidation(String UserName) {
        return DataAccessLayer.makePlayerTwoBusy(UserName);
    }
    public Integer getPlayerScoreValidation(String UserName){
        try {
            return DataAccessLayer.getPlayerScore(UserName);
        } catch (SQLException ex) {
            Logger.getLogger(ServerConnection.class.getName()).log(Level.SEVERE, null, ex);
            return 0;
        }
    }
    public Boolean updateScoreValidation(UserDTO obj)
    {
        return DataAccessLayer.updateScore(obj);
    }

}
