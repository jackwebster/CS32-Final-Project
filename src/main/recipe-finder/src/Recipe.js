import React, {useEffect, useState} from 'react';
import Button from 'react-bootstrap/Button';
import SimilarRecipe from "./SimilarRecipe";
import Rating from '@material-ui/lab/Rating';
import {Link} from "react-router-dom";
import axios from "axios";
import Loader from 'react-loader-spinner';
import {useAuth} from "./contexts/AuthContext";

function Recipe(props) {

    // useState variable for name
    const [name, setName] = useState("Recipe Name");

    // useState hook for default values of ratings
    const [value, setValue] = React.useState(2.5);

    // useState hooks for all initial info about the recipe
    const [ingredients, setIngredients] = useState("ingredients");
    const [preparation, setPreparation] = useState("preparation");

    // useState hooks for loader component
    const [loading, setLoading] = useState(false);
    const [loader, setLoader] = useState(false);
    const [opacity, setOpacity] = useState(1.0);

    // useState hooks for similar labels
    const [similarLabels, setSimilarLabels] = useState([]);
    const [photos, setPhotos] = useState([]);
    const [url, setUrl] = useState("");

    // authentication
    const {currentUser, logout} = useAuth()


    // Axios Requests

    /*
     * Makes an axios request for finding similar recipes
     */
    const findSimilar = (name, event) => {

        const toSend = {
            recipe: name,
            user: currentUser.email
        };

        let config = {
            headers: {
                "Content-Type": "application/json",
                'Access-Control-Allow-Origin': '*',
            }
        }

        axios.post(
            "http://localhost:4567/recipe",
            toSend,
            config
        )
            .then(response => {
                let object = response.data;
                let labels = [];
                let pics = [];

                setValue(2.5);


                //set up suggestions!
                for (let key in object) {
                    let recipe = object[key]
                    let recipeIngredients = [];
                    //set up recipe itself
                    if (key == 'recipe') {
                        for (const i of Object.keys(recipe)) {
                            if (endsWithNumber(i)){
                                recipeIngredients.push(recipe[i]);
                            }
                        }
                        //set currentRating
                        setValue(recipe["rating"]);
                        setName(recipe["title"].replace(/\b\w/g, l => l.toUpperCase()));
                        setIngredients(recipeIngredients.toString());
                        setPreparation(recipe["instructions"]);
                        setUrl(recipe["url"]);
                    } else {  //store it dynamically to be accessed by SimilarRecipe objects
                        //get image and name
                        labels.push(recipe["recipeName"]);
                        pics.push(recipe["src"])
                    }
                }
                setSimilarLabels(labels);
                setPhotos(pics);
                setLoading(false);


            })

            .catch(function (error) {
                console.log(error);
            });
    }

    /*
     * Makes an axios request for rating the recipe
     */
    const rateRecipe = (rating, event) => {
        const toSend = {
            rating: rating,
            recipe: name
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
            })

            .catch(function (error) {
                console.log(error);
            });
    }


    // style details for root page
    const rootStyle = {
        backgroundColor: "white",
        height: '100vh',
        opacity: opacity
    }

    //helper function w/ axios request
    function endsWithNumber( str ){
        return isNaN(str.slice(-1)) ? false : true;
    }

    //helper function that dynamically loads in new recipes
    function setNewRecipe(name){
        setLoading(true);
        findSimilar(name);
    }

    //opens a link to recipe
    function openPage() {
        window.open(url);
    }

    //useEffect hook for initial render
    useEffect(() => {
        setLoading(true);
         findSimilar(props.location.state.name);
    }, [])


    // useEffect hook for loading
    useEffect(() => {
        if(loading){
            setLoader(true);
            setOpacity(0.5)
        } else{
            setLoader(false);
            setOpacity(1.0);
        }
    }, [loading]);


    return (
        <div style={rootStyle} className="Recipe">
            {/*loader for loading*/}
            <div className={"centered"}>
                <Loader
                    type="TailSpin"
                    color="#2776ED"
                    height={400}
                    width={400}
                    visible={loader}
                />
            </div>
            {/*dynamic header*/}
            <h1 style={{top: 25, color: "#000"}}><b>{name}</b></h1>
            {/*two buttons on side of page*/}
            <Link to={"/RecipeSelection"}>
            <Button variant="success" size= "lg" style={{position: "absolute", left: 50, top: 25, width: 175}}>Back to Search </Button>
            </Link>
            <Link to={"/fridge"}>
                <Button variant="success" size= "lg" style={{position: "absolute", left: 50, top: 85, width: 175}}>Back to Fridge</Button>
            </Link>
            <Button variant="primary" size= "lg" style={{position: "absolute", right: 50, top: 25}} onClick={openPage}>Cook!</Button>

            <div style={{position: "relative", left: -500, marginTop: 150, marginLeft: 0, width: 1200}}>

                {/*ratings in header*/}
                <Rating
                    style={{position: "relative", top: 40, left: 1250}}
                    name="simple-controlled"
                    value={value}
                    precision={0.5}
                    size={"large"}
                    onChange={(event, newValue) => {
                        setValue(newValue);
                        rateRecipe(newValue);
                    }}
                />

                {/*ingredients header*/}
                <h2 className={"recipe"}><u>Ingredients</u></h2>


                {/*paragraph for ingredients*/}
                <p className={"inside-paragraph"}>{ingredients}</p>

                {/*instructions header*/}
                <h2 className={"recipe"}><u>Preparation</u></h2>

                {/*paragraph for instruction*/}
                <p className={"inside-paragraph"}>{preparation}</p>

                {/*similar recipes header*/}
                <h2 className={"recipe"} style={{position: "relative", bottom: -100}}><u>Similar Recipes</u></h2>

            </div>
            {/*three similar recipes as suggestions*/}
            <div style={{position:"absolute", left: 0, right:0}} className={"flex-container"}>

                <SimilarRecipe label={similarLabels[0]} photo={photos[0]} set={setNewRecipe}/>
                <SimilarRecipe label={similarLabels[1]} photo={photos[1]} set={setNewRecipe}/>
                <SimilarRecipe label={similarLabels[2]} photo={photos[2]} set={setNewRecipe}/>
            </div>
        </div>
    );
}

export default Recipe;