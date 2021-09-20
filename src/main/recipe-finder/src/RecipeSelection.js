import {Link} from "react-router-dom";
import Button from "react-bootstrap/Button";
import { makeStyles } from '@material-ui/core/styles';
import {Card, CardActions, CardMedia, CardContent, Typography} from '@material-ui/core';
import axios from "axios";
import {useEffect, useState} from "react";


let alt = false;

//for card styles
const useStyles = makeStyles({
    root: {
        minWidth: 275,
        backgroundColor: "#d9d9d9",
        marginLeft: 25,
        marginTop: 25,
        marginRight:25
    },
    title: {
        fontSize: 14,
    },
    pos: {
        marginBottom: 12,
    },
    media: {
        height: 175,
        width: 600
    },
});

function RecipeSelection() {
    //useState hook for list of recipes to present to user
    let [recipes, setRecipes] = useState([]);
    let [recipesToShow, setRecipesToShow] = useState(false);

    // Axios Requests

    /*
     * Makes an axios request for finding suggestions
     */
    const findSuggestions = (event) => {

        const toSend = {
            //empty
        };

        let config = {
            headers: {
                "Content-Type": "application/json",
                'Access-Control-Allow-Origin': '*',
            }
        }

        axios.post(
            "http://localhost:4567/find-suggestions",
            toSend,
            config
        )
            .then(response => {
                let object = response.data;
                recipes = []
                if (!("error" in object)) {
                    //iterate through suggestions
                    for (var i = 0; i < Object.keys(object).length; i++) {
                        recipes.push(object["suggestion-" + i]);
                    }
                    setRecipesToShow(true);
                } else{
                    setRecipesToShow(false);
                }
                setRecipes(recipes);

            })

            .catch(function (error) {
                console.log(error);
            });
    }

    // style details for root page
    const rootStyle = {
        backgroundColor: "white",
        height: '100vh'
    }

    // style details for list of recipes
    const style = {
        backgroundColor: "#2776ED",
        height: 600,
        width: 1300,
        position: "absolute",
        top: 100,
        left: 75
    }

    // style details for inner scroll div
    const innerStyle = {
        backgroundColor: "#FFF",
        height: 550,
        width: 1250,
        position: "relative",
        top: 25,
        left: 25,
        overflow: "scroll"
    }

    const classes = useStyles();

    //useEffect hook for first render
    useEffect(() => {
        findSuggestions();
    }, []);


    return (
        <div style={rootStyle}>
            {/*dynamic header*/}
            <h1 style={{marginTop: 25}}>Select a Recipe</h1>
            {/*button on side of page*/}
            <Link to={"/Fridge"}>
                <Button variant="success" size= "lg" style={{position: "absolute", left: 50, top: 25}}>Back to Fridge</Button>
            </Link>
            <div style={style}>
                <div style={innerStyle}>
                    {recipesToShow ? recipes.map((r) => {
                        // alternate sides for aesthetic appeal
                        if (alt){
                            alt = false;
                            return <Card className={classes.root}>
                                <div style={{width: 600, float: "left"}}>
                                    <CardContent>
                                        <Typography variant="h5" component="h2">
                                            {/*recipe name*/}
                                            {r.recipeName}
                                        </Typography>
                                        <Typography variant="body2" component="p">
                                            <br/>
                                            {/*chef name*/}
                                            By: {r.chef}
                                        </Typography>
                                    </CardContent>
                                    <CardActions>
                                        {/*<Link to={{"/recipe"} recipeName={"YEET"}>*/}
                                        <Link
                                            to={{
                                                pathname: "/recipe",
                                                state: { name: r.recipeName}
                                            }}
                                        >
                                        <Button size="small">See Recipe</Button>
                                        </Link>
                                    </CardActions>
                                </div>
                                <div style={{position: "relative", marginLeft: 650}}>
                                    <CardMedia
                                        className={classes.media}
                                        image={r.src}
                                        title={r.recipeName}
                                    />
                                </div>
                            </Card>
                        } else {
                            alt = true;
                            return <Card className={classes.root}>
                                <div style={{width: 600, float: "right"}}>
                                    <CardContent>
                                        <Typography style={{textAlign: "right"}} variant="h5" component="h2">
                                            {/*recipe name*/}
                                            {r.recipeName}
                                        </Typography>
                                        <Typography style={{textAlign: "right"}}variant="body2" component="p">
                                            <br/>
                                            {/*chef name*/}
                                            By: {r.chef}
                                        </Typography>
                                    </CardContent>
                                    <CardActions>
                                        <Link
                                            to={{
                                                pathname: "/recipe",
                                                state: { name: r.recipeName}
                                            }}
                                        >
                                            <Button style={{marginLeft:475}} size="small">See Recipe</Button>
                                        </Link>
                                    </CardActions>
                                </div>
                                <div style={{position: "relative", marginRight: 650, marginTop:5}}>
                                    <CardMedia
                                        className={classes.media}
                                        image={r.src}
                                        title={r.recipeName}
                                    />
                                </div>
                            </Card>
                        }
                    }) : <h4 className="text-center pt-5" >No Recipes To Show</h4> }
                </div>
            </div>
        </div>
    );
}

export default RecipeSelection;
