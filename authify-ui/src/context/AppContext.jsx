import React, {createContext} from "react";
import {AppConstants} from "../util/constants.js";
import axios from "axios";
import {toast} from "react-toastify";


export const AppContext = createContext();


export const AppContextProvider = (props) => {

    const backendUrl = AppConstants.BACKEND_URL;
    const [isLoggedIn, setIsLoggedIn] = React.useState(false);
    const [userData, setUserData] = React.useState(false);

    const getUserData = async () => {

        try {
            const response = await axios.get(`${backendUrl}/profile`);

            if (response.status === 200) {
                setUserData(response.data);
            }else{
                toast.error("unable to retrieve user's profile");
            }
        }catch(err){
            toast.error(err.message);
        }

    }

    const contextValue = {
        backendUrl,
        isLoggedIn,setIsLoggedIn,
        userData,setUserData,
        getUserData,
    }

    return (
        <AppContext.Provider value={contextValue}>
            {props.children}
        </AppContext.Provider>
    )
}