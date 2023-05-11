/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package nasr;

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
import nasr.Messages;
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

                        }
                        
                        else if (msg.equals(Messages.userExistRequest)) {
                            obj = (UserDTO) objectinputstream.readObject();
                            sendMessage(Messages.userExistRespons, checkUserExist());
                        } else if (msg.equals(Messages.offlineUsersRequest)) {
                            obj = (UserDTO) objectinputstream.readObject();
                            sendMessage(Messages.offlineUsersResponse, getOfflineUsers());
                        } else if (msg.equals(Messages.getAllInfoRequest)) {
                            obj = (UserDTO) objectinputstream.readObject();
                            sendMessage(Messages.getAllInfoResponse, getUserInfo());
                        }else if (msg.equals(Messages.getOnlinePlayerNumRequest)) {
                            obj = (UserDTO) objectinputstream.readObject();
                            sendMessage(Messages.getOnlinePlayerNumResponse, getOnlinePlayersNum());
                        }else if (msg.equals(Messages.getbusyPlayersNumRequest)) {
                            obj = (UserDTO) objectinputstream.readObject();
                            sendMessage(Messages.getbusyPlayersNumResponse, getbusyPlayersNum());
                        }
                        
                        
                    } catch (ClassNotFoundException | IOException e) {
                        e.printStackTrace();
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

    public Boolean registrationValidation() {
        // insert in database and return true if sucess and false if failed
        System.out.println("Login Validation Test");
        return false;
    }

    public Boolean checkUserExist() throws SQLException {
        Boolean isExist = false;
        UserDTO user=(UserDTO) obj;
        System.out.println("checkUserExist Test");
        if (DataAccessLayer.checkIfUserExist(user.getUserName())) {
            isExist = true;
        }
        return isExist;
    }

    public ArrayList<UserDTO> getOfflineUsers() throws SQLException {

        ArrayList<UserDTO> UserList = DataAccessLayer.getOfflinePlayers();
        if (UserList.isEmpty()) {
            UserList = new ArrayList<UserDTO>();
        }
        return UserList;
    }

    public Object getUserInfo() throws SQLException {
        
        Object user = DataAccessLayer.getAllInfo(((UserDTO) obj).getUserName());

        return user;
    }
    public Integer getOnlinePlayersNum() throws SQLException{
        Integer onlinePlayersNum=0;
        onlinePlayersNum=DataAccessLayer.getOnlinePlayersNum();
    return onlinePlayersNum;
    }
    
    public Integer getbusyPlayersNum() throws SQLException{
         Integer busyPlayersNum=0;
         busyPlayersNum=DataAccessLayer.getbusyPlayersNum();
         return busyPlayersNum;
    }
}
