import React, {useRef, useState} from 'react'
import {Form, Button, Card, Alert} from 'react-bootstrap'
import {useAuth} from "./contexts/AuthContext";
import {Link, useHistory} from "react-router-dom";

export default function UpdatePassword() {
    //authentication variables
    const passwordRef = useRef()
    const passwordConfirmRef = useRef()
    //update password function
    const {updatePassword} = useAuth()
    const [error, setError] = useState("")
    const [loading, setLoading] = useState(false)
    const history = useHistory()

    /**
     * This method handles submission and updates password
     * @param e representing the event
     */
    function handleSubmit(e) {
        e.preventDefault()
        if (passwordRef.current.value !== passwordConfirmRef.current.value) {
            return setError("Passwords do not match")
        }

        const promises = []
        setLoading(true)
        setError("")

        if (passwordRef.current.value) {
            promises.push(updatePassword(passwordRef.current.value))
        }


        Promise.all(promises)
            .then(() => {
                history.push("/fridge")
            })
            .catch(() => {
                setError("Failed to update account")
            })
            .finally(() => {
                setLoading(false)
            })
    }

    return(
        <>
            <Card>
                <Card.Body>
                    <h2 className={"text-center mb-3"}> Update Profile</h2>
                    {error && <Alert variant={"danger"}> {error} </Alert>}
                    <Form onSubmit={handleSubmit}>
                        <Form.Group id="password">
                            <Form.Label>Password</Form.Label>
                            <Form.Control type={"password"} ref={passwordRef} required/>
                        </Form.Group>
                        <Form.Group id="password-confirm">
                            <Form.Label>Password Confirmation</Form.Label>
                            <Form.Control type={"password"} ref={passwordConfirmRef} required/>
                        </Form.Group>
                        <Button disabled={loading} className={"w-100"} type={"submit"}>
                            Update
                        </Button>
                    </Form>
                </Card.Body>
            </Card>
            <div className={"w-100 text-center mt-2"}>
                <Link to={"/fridge"}>Cancel</Link>
            </div>
        </>
    )
}