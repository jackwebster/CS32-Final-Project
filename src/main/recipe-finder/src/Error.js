import React from 'react';
import {Button} from 'react-bootstrap'

export default function Error() {
    return (
        <>
            <div style={{position: "relative", top: -100}}>
                <h1 className={"text-dark font-weight-bold"}>Page not found</h1>
            </div>
            <div className={"d-flex justify-content-center mb-3 mt-3"}>
                <Button href={"/fridge"} className={"nav-link btn-primary"}>
                    Return Home
                </Button>
            </div>
        </>
    )
}