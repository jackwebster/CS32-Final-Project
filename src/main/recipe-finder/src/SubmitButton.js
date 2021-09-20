import Button from 'react-bootstrap/Button';
import React from "react";


function SubmitButton(props) {

    return <Button variant="success" size= "lg" onClick={
        // click
        props.onClick

    }>{props.label}!</Button>;
}

export default SubmitButton;