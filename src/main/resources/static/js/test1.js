
function showAlert() {
    alert("The button was clicked!");
}

function exec() {
    $.get("run", function(data, status){
        alert("Data: " + data + "\nStatus: " + status);
    });
}

function exec2() {
    $.get("run?action=echo", function(data, status){
        alert("Data: " + data + "\nStatus: " + status);
    });
}

function exec3(codeAction,projectId) {
    $.get("run?action="+codeAction+"&projetId="+projectId, function(data, status){
        console.log("Data: ", data , "Status: " , status);
    });
}

