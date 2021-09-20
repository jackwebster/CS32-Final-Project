import {Alert, Button, Card} from "react-bootstrap";
import {Link, useHistory} from "react-router-dom";
import React, {useEffect, useState} from "react";
import {useAuth} from "./contexts/AuthContext";
import axios from "axios";
import Rating from "@material-ui/lab/Rating";
import Modal from "react-bootstrap/Modal";

function Profile() {

    // useState hooks for authentication
    const [error, setError] = useState("")
    const {currentUser, logout} = useAuth()
    const history = useHistory()

    //useState hooks for user info
    const [name, setName] = useState("");
    const [ratedRecipes, setRatedRecipes] = useState([]);
    const [noRecipesToShow, setNoRecipes] = useState(false);
    const [noIngredientsToShow, setNoIngredients] = useState(false);
    const [ratedIngredients, setRatedIngredients] = useState([]);

    //useState loading hook
    const [loading, setLoading] = useState(false)

    // useState variables for deletion modal
    const [modalIsOpen, setModalIsOpen] = useState(false);


    // handlers for modals
    const handleClose = () => setModalIsOpen(false);
    const handleCloseDelete = () => {
        handleDelete();
    };


    // Axios Requests

    //axios request to rate an ingredient
    const rateIngredient = (rating, event) => {
        let curr = event.target.getAttribute("name")
        const toSend = {
            ingredient: curr,
            rating: rating
        };

        let config = {
            headers: {
                "Content-Type": "application/json",
                'Access-Control-Allow-Origin': '*',
            }
        }

        axios.post(
            "http://localhost:4567/rate-ingredient",
            toSend,
            config
        )
            .then(response => {
                let ratings = {}
                for (var key in ratedIngredients) {
                    ratings[key] = ratedIngredients[key];
                }
                ratings[curr] = rating;

                setRatedIngredients(ratings);
                //nothing
            })

            .catch(function (error) {
                console.log(error);
            });
    }
    /*
        * Makes an axios request for rating the recipe
        */
    const rateRecipe = (rating, event) => {
        let curr = event.target.getAttribute("name")

        const toSend = {
            rating: rating,
            recipe: curr
        };

        let config = {
            headers: {
                "Content-Type": "application/json",
                'Access-Control-Allow-Origin': '*',
            }
        }

        axios.post(
            "http://localhost:4567/rate-recipe",
            toSend,
            config
        )
            .then(response => {
                let ratings = {}
                for (var key in ratedRecipes) {
                    ratings[key] = ratedRecipes[key];
                }
                ratings[curr] = rating;
                setRatedRecipes(ratings);
            })

            .catch(function (error) {
                console.log(error);
            });
    }

    /*
     * Makes an axios request for user info
     */
    const getName = (email) => {

        const toSend = {
            name: email
        };

        let config = {
            headers: {
                "Content-Type": "application/json",
                'Access-Control-Allow-Origin': '*',
            }
        }

        axios.post(
            "http://localhost:4567/profile",
            toSend,
            config
        )
            .then(response => {
                let name = response.data["name"];
                let recipes = response.data["recipes"];
                let ingredients = response.data["ingredients"];
                if("error" in recipes) {
                    setNoRecipes(true);
                } else {
                    setNoRecipes(false);
                }
                if("error" in ingredients) {
                    setNoIngredients(true);
                } else {
                    setNoIngredients(false);
                }

                //update variables
                setName(name);
                setRatedRecipes(recipes);
                setRatedIngredients(ingredients);
            })
            .catch(function (error) {
                console.log(error);
            });
    }

    /**
     * This method deletes the user account and removes the user data from the SQL database
     * @param email
     */
    const deleteUser = (email) => {

        const toSend = {
            name: email
        };

        let config = {
            headers: {
                "Content-Type": "application/json",
                'Access-Control-Allow-Origin': '*',
            }
        }

        axios.post(
            "http://localhost:4567/delete-user",
            toSend,
            config
        )
            .then(() => {
                currentUser.delete()
                    .then(() => {
                        history.push("/login")
                    }).catch(function(error) {
                    console.log(error)
                });
            })
            .catch(function (error) {
                console.log(error);
            });
        setLoading(false)
    }

    const rootStyle = {
        backgroundColor: "white",
        height: '100vh'
    }

    // used to display error message (in which case map is a string)
    function checkIfMap(map, key) {
        if(map[key] != undefined) {
            return parseFloat(map[key]);
        } else {
            return "";
        }
    }
    async function handleLogout() {
        setError("")
        try {
            await logout()
            history.push("/login")
        } catch {
            setError("Failed to log out")
        }
    }

    // handles deletion of user in firebase
    async function handleDelete() {
        setLoading(true)
        deleteUser(currentUser.email)
    }

    //useEffect hook for initial render
    useEffect(() => {
        getName(currentUser.email);
    },[]);


    return (
        <div>

            {/*Modal for user account deletion*/}
            <Modal show={modalIsOpen} onHide={handleClose}>
                <Modal.Header closeButton>
                    <Modal.Title>Delete account?</Modal.Title>
                </Modal.Header>
                <Modal.Body> <b>WARNING: Your account will be permanently deleted.</b> </Modal.Body>
                <Modal.Footer>
                    <Button variant="light" onClick={handleClose}>
                        Cancel
                    </Button>
                    <Button variant="danger" onClick={handleCloseDelete}>
                        Delete
                    </Button>
                </Modal.Footer>
            </Modal>

            <Link to={"/fridge"}>
                <Button variant="success" size= "lg" style={{position: "absolute", left: 50, top: 25}}>Back to Fridge</Button>
            </Link>
            <Card>
                <Card.Body>
                    <h2 className={"text-center mb-4"}>Profile</h2>
                    {error && <Alert variant={"danger"}> {error} </Alert>}
                    <strong>Name: </strong> {name}
                    <br/>
                    <strong>Email: </strong> {currentUser.email}
                    <Link to={"/update-password"} className={"btn btn-primary w-100 mt-3"}>
                        Update Password
                    </Link>
                </Card.Body>
            </Card>
            <br/>
            <Card>
                <Card.Body>
                    <h2 className={"text-center mb-4"}>Rated Recipes</h2>
                    { noRecipesToShow ? <h5 className="text-center">No rated recipes</h5> :
                        Array.from(Object.keys(ratedRecipes)).map(r =>
                        <div>
                            <p>{r}</p>
                            <Rating
                                name={r}
                                precision={0.5}
                                size={"small"}
                                onChange={(event, newValue) => {
                                    rateRecipe(newValue, event);
                                }}
                                value={checkIfMap(ratedRecipes, r)}
                            />
                        </div>
                        ) }
                </Card.Body>
            </Card>
            <br/>
            <Card>
                <Card.Body>
                    <h2 className={"text-center mb-4"}>Rated Ingredients</h2>
                    {noIngredientsToShow ? <h5 className="text-center"> No rated ingredients</h5> :
                        Array.from(Object.keys(ratedIngredients)).map(r =>
                        <div className="ingredient">
                            <p>{r}</p>
                            <Rating
                                name={r}
                                precision={0.5}
                                size={"small"}
                                onChange={(event, newValue) => {
                                    rateIngredient(newValue, event);
                                }}
                                value={checkIfMap(ratedIngredients, r)}
                            />
                        </div>
                    ) }
                </Card.Body>
            </Card>
            <div className={"w-100 text-center mt-2"}>
                <Button variant={"link"} onClick={handleLogout}> Log Out</Button>
            </div>
            <div className={"w-100 text-center mt-2"}>
                <Button variant={"danger"} onClick={() => {
                    setModalIsOpen(true);
                }} disabled={loading}> Delete Account</Button>
            </div>
        </div>
    );
}

export default Profile;