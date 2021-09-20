
function TextBox(props) {

    // Changes value of text box
    const value = (event) => {
        let val = event.target.value;
        props.input(val);
        props.setCurr(val);
        props.onKeyUp();
        console.log("triggered");
    }

    // Style of label
    const labelStyle = {
        fontSize: 20,
        fontFamily: "Avenir",
        textAlign: "left",
        position: "relative",
        left: 0,
        bottom:0
    }

    return (
        <div>
            <label style={labelStyle}>{props.label}</label>
            <br/>
            <input value={props.val} style={{fontSize:24}} className="inputBox" type={"text"}
                   onChange={value} id={"inputBox"} autoComplete={"off"}></input>
        </div>
    );
}

export default TextBox;
