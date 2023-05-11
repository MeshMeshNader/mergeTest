/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package tictactoeclient;

import java.io.Serializable;

/**
 *
 * @author meshm
 */
public class Messages implements Serializable {

    public static String loginResponse = "LoginResponse";
    public static String loginRequest = "LoginRequest";
    public static String registrationResponse = "RegistrationResponse";
    public static String registrationRequest = "RegistrationRequest";
    public static String getOfflinePlayersNumRequest = "GetOfflinePlayersNumRequest";
    public static String getOfflinePlayersNumResponse = "GetOfflinePlayersNumResponse";
    public static String logoutRequest = "LogoutRequest";
    public static String logoutResponse = "LogoutResponse";
    public static String makePlayerOneBusyRequest = "MakePlayerOneBusyRequest";
    public static String makePlayerOneBusyResponse = "MakePlayerOneBusyResponse";
    public static String makePlayerTwoBusyRequest = "MakePlayerTwoBusyRequest";
    public static String makePlayerTwoBusyResponse = "MakePlayerTwoBusyResponse";
    public static String getPlayerScoreRequest = "getPlayerScoreRequest";
    public static String getPlayerScoreResponse = "getPlayerScoreResponse";
    public static String updateScoreRequest = "updateScoreRequest";
    public static String updateScoreResponse = "updateScoreResponse";
}
