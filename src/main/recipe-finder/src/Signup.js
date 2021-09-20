import React, {useRef, useState} from 'react'
import {Form, Button, Card, Alert} from 'react-bootstrap'
import {useAuth} from "./contexts/AuthContext"
import {Link, useHistory} from "react-router-dom"
import axios from "axios"

export default function Signup() {
    //authentication variables
    const nameRef = useRef()
    const emailRef = useRef()
    const passwordRef = useRef()
    const passwordConfirmRef = useRef()
    //signup function
    const {signup} = useAuth()
    //error message
    const [error, setError] = useState("")
    const [loading, setLoading] = useState(false)
    const history = useHistory()

    /**
     * Axios request for making a new user in firebase
     * @param name
     * @param email
     */
    const createUser = (name, email) => {
        console.log(emailRef.current.value)
        const toSend = {
            name: name,
            email: email
        };
        let config = {
            headers: {
                "Content-Type": "application/json",
                'Access-Control-Allow-Origin': '*',
            }
        }
        // sending this information to backend
        axios.post(
            "http://localhost:4567/newUserSignup",
            toSend,
            config
        ).then(() => {
            history.push("/fridge")
        })
            .catch(function (error) {
                console.log(error);
            });
    }

    /**
     * This function handles submission of a signup request
     * @param e
     * @returns error if applicable
     */
    async function handleSubmit(e) {
        e.preventDefault()

        if (passwordRef.current.value !== passwordConfirmRef.current.value) {
            //return here beause we don't want to continue with signup, exit function
            return setError("Passwords do not match")
        }
        //will get a firebase error if password is less than 6 characters long
        if (passwordRef.current.value.length < 6){
            return setError("Password must be at least 6 characters long")
        }

        try {
            setError('')
            //don't want user to click sign up button multiple times
            setLoading(true)
            await signup(emailRef.current.value, passwordRef.current.value)
            await createUser(nameRef.current.value, emailRef.current.value)
        } catch (error) {
            setError("Failed to create an account")
            console.log(error)
            setLoading(false)
        }
    }

    return(
        <>
          <Card>
              <Card.Body>
                <h2 className={"text-center mb-3"}> Sign Up</h2>
                  {error && <Alert variant={"danger"}> {error} </Alert>}
                <Form onSubmit={handleSubmit}>
                    <Form.Group id="name">
                        <Form.Label>Name</Form.Label>
                        <Form.Control type={"name"} ref={nameRef} required/>
                    </Form.Group>
                    <Form.Group id="email">
                        <Form.Label>Email</Form.Label>
                        <Form.Control type={"email"} ref={emailRef} required/>
                    </Form.Group>
                    <Form.Group id="password">
                        <Form.Label>Password</Form.Label>
                        <Form.Control type={"password"} ref={passwordRef} required/>
                    </Form.Group>
                    <Form.Group id="password-confirm">
                        <Form.Label>Password Confirmation</Form.Label>
                        <Form.Control type={"password"} ref={passwordConfirmRef} required/>
                    </Form.Group>
                    <Button disabled={loading} className={"w-100"} type={"submit"}>
                        Sign Up
                    </Button>
                </Form>
              </Card.Body>
          </Card>
          <div className={"w-100 text-center mt-2"}>
              Already have an account? <Link to={"/login"}>Log In</Link>
          </div>
        </>
    )
}