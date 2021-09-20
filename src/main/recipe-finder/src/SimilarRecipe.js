
function SimilarRecipe(props) {

    let style = {
        backgroundColor: "#888",
        height: 200,
        width: 200,
        backgroundImage: 'url('+ props.photo + ')',
        backgroundSize: "cover",
        cursor: "pointer"
    }

    return (
        <div style={style} className="flex-item" onClick={e => {
            props.set(props.label);
        }}>
            <div style={{marginTop: 75, backgroundColor: "black"}}>
            <h5 style={{color: "white"}}>{props.label}</h5>
            </div>
        </div>
    );
}

export default SimilarRecipe;