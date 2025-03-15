const { Server } = require("socket.io");
const http = require("http");
const express = require("express");
const crypto = require("crypto");


const app = express();
const server = http.createServer(app);
const io = new Server(server, {
    cors: {
        origin: "*",
        methods: ["GET", "POST"]
    }
});


users = {} // users[socket.id] = {name:"abc", image:"a", mark:"X"}
rooms = {} // rooms[room.id] = {player1 = socket.id, player2= socket.id, board = ["X", "O", ...], turn = ""}

usersOnline = 0;



app.get("/", (req, res) => {
    res.send("Hello");
})



io.on("connection", (socket) => {

    console.log(`new client ${socket.id}`);
    usersOnline = usersOnline + 1;

    socket.on("create_user", (data) => {
        users[socket.id] = { name: data.name, image: data.image }
        console.log(data);
        console.log(usersOnline);
    });

    socket.on("create_room", () => {
        const id = crypto.randomBytes(3).toString('hex');
        users[socket.id].mark= "X";
        rooms[id] = { player1: socket.id,player2:null, board: [], turn: "X" };
        socket.join(id);
        console.log("room created by" ,users[socket.id]);
        console.log("room id by", users[socket.id].name, "is", id)

        io.to(id).emit("room_created", {id:id});
    });


    socket.on("join_room", (data) => {
        let id = data.id
        console.log("room join id by", users[socket.id].name, "is", id)
        if (rooms[id].player2 == null) {
            users[socket.id].mark = "O";
            rooms[id].player2 = socket.id;

            socket.join(id);

            console.log("room joined by" ,users[socket.id]);
            console.log(rooms[id])
            
            io.to(id).emit("user_joined", users[socket.id]);
            // socket.to(id).emit("start_game", rooms[id]);
        }
    });


    socket.on("play_move", (id, index) => {
        room = rooms[id]
        if (room[board][index] == undefined) {
            room[board][index] = room[turn];
            room[turn] = (room[turn] == "X") ? "O" : "X";
            socket.to(id).emit("move_played", (room))
        }
    });


    socket.on("disconnect", () => {
        console.log("User Disconnected", users[socket.id])
        delete users[socket.id]
        usersOnline = usersOnline - 1;
        console.log(usersOnline);


    });


});







server.listen(3000, "192.168.99.37", () => {

    console.log("server running at 192.168.99.37:3000");

});


