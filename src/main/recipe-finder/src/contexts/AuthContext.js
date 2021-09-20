import React, {useContext, useState, useEffect} from 'react'
import {auth} from "../firebase"

const AuthContext = React.createContext()


export function useAuth() {
    return useContext(AuthContext)
}

/**
 * This function contains all of our firebase functions we use for logging in and keeps
 * track of the current user
 * @param children
 * @returns {JSX.Element}
 * @constructor
 */
export function AuthProvider({children}){

    const [currentUser, setCurrentUser] = useState()
    //by default we are loading
    const [loading, setLoading] = useState(true)

    //if don't want to use firebase, we can just edit these signup and login functions
    function signup(email, password) {
        //return these functions because method returns a promise
        return auth.createUserWithEmailAndPassword(email, password)
    }

    function login(email, password) {
        return auth.signInWithEmailAndPassword(email, password)
    }

    function logout() {
        return auth.signOut()
    }

    function resetPassword(email) {
        return auth.sendPasswordResetEmail(email)
    }

    // function updateEmail(email) {
    //     return currentUser.updateEmail(email)
    // }

    function updatePassword(password) {
        return currentUser.updatePassword(password)
    }

    //only want to call once so we use useEffect here
    useEffect(() => {
        const unsubscribe = auth.onAuthStateChanged(user => {
            //did verification to see if there is a user, so set to false
            setCurrentUser(user)
            setLoading(false)
        })
        //unsubscribes us whenever we unmount this component
        return unsubscribe
    }, [])

    const value = {
        currentUser,
        login,
        signup,
        logout,
        resetPassword,
        //updateEmail,
        updatePassword
    }

    return(
        <AuthContext.Provider value={value}>
            {/*only want to render children if we are not loading */}
            {!loading && children}
        </AuthContext.Provider>
    )
}