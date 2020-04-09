package com.company;


import com.google.gson.Gson;

import javax.imageio.IIOException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;


class Card {
    int fill, count, shape, color;


    @Override
    public int hashCode() {
        return Objects.hash(fill, count, shape, color);
    }
    @Override

    public boolean equals(Object o){
        Card card = (Card) o;


        if (card.fill == this.fill && card.color == this.color && card.count == this.count && card.shape == this.shape){
            return true;
        }
        return false;

    }


    public Card getThird(Card card){
        Card thirdCard = new Card();
        int sum = 6;
        int cur_sum = 0;

        if (card.count == this.count){
            thirdCard.count = card.count;
        }
        else {
            cur_sum = card.count + this.count;
            thirdCard.count = sum - cur_sum;
            cur_sum = 0;
        }

        if (card.color == this.color){
            thirdCard.color = card.color;
        }
        else {
            cur_sum = card.color + this.color;
            thirdCard.color = sum - cur_sum;
            cur_sum = 0;
        }

        if (card.shape == this.shape){
            thirdCard.shape = card.shape;
        }
        else {
            cur_sum = card.shape + this.shape;
            thirdCard.shape = sum - cur_sum;
            cur_sum = 0;
        }

        if (card.fill == this.fill){
            thirdCard.fill = card.fill;
        }
        else {
            cur_sum = card.fill + this.fill;
            thirdCard.fill = sum - cur_sum;
            cur_sum = 0;
        }

        return thirdCard;

    }

    public String toStringCard(){
        String[] colors  = new String[] {"blue", "green", "red"};
        String[] shapes  = new String[] {"rect", "rhombus", "wave"};
        String[] fills  = new String[] {"contour", "hatch", "full"};
        String color = "";
        String shape = "";
        String fill = "";
        switch (this.color){
            case 1:
                color = colors[0];
                break;
            case 2:
                color = colors[1];
            case 3:
                color = colors[2];

        }

        switch (this.shape){
            case 1:
                shape = shapes[0];
                break;
            case 2:
                shape = shapes[1];
            case 3:
                shape = shapes[2];

        }

        switch (this.fill){
            case 1:
                fill = fills[0];
                break;
            case 2:
                fill = fills[1];
            case 3:
                fill = fills[2];

        }
        String result = "Card: fill = " + this.fill + ", count = " + this.count + ", shape = " +this.shape+", color = " + this.color;
        return result;

    }

}

class Cards{
    Card[] cards;
}

class Request {
    String action, nickname;
    ArrayList<Card> cards = new ArrayList<>();
    // предусмотреть конструкторы для каждого типа запросов (3 шт)
    public Request(String action, String nickname) {
        this.action = action;
        this.nickname = nickname;
    }

    public Request(String action,int token){
        this.action = action;
        this.token = token;
    }

    public Request(String action, int token, ArrayList<Card> cards){
        this.action = action;
        this.token = token;
        this.cards = cards;
    }

    int token;

}

class Response {
    String status;
    int token,points,cards_left;
    ArrayList<Card> cards;



}

public class Main {

    public static Response serverRequest(Request req) throws IOException {

        // 2) доработать функцию и обработать исключения
        // измените номер порта на тот, что у Вашего сервера
        String set_server_url = "http://194.176.114.21:8059";
        Gson gson = new Gson();
        URL url = new URL(set_server_url);

        HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
        urlConnection.setDoOutput(true); // setting POST method
        //req = new Request("register", "Petya1");

        OutputStream out = urlConnection.getOutputStream();
        out.write(gson.toJson(req).getBytes());
        InputStream stream = urlConnection.getInputStream();
        Response response = gson.fromJson(new InputStreamReader(stream), Response.class);
        return response;
    }

    public static ArrayList<Card> findSet(ArrayList<Card> cards) {

        Set<Card> cardSet = new HashSet<Card>();
        for (int i = 0; i < cards.size(); i++) {
            cardSet.add(cards.get(i));
        }
        ArrayList<Card> set_card = new ArrayList<>();

        outterLoop: for (int i = 0; i < cards.size() - 1; i++) {
            for (int j = i + 1; j < cards.size(); j++) {
                Card thirdCard = new Card();
                thirdCard = cards.get(i).getThird(cards.get(j));
                if (cardSet.contains(thirdCard)) {

                    set_card.add(cards.get(i));
                    set_card.add(cards.get(j));
                    set_card.add(thirdCard);

                    break outterLoop;


                }


            }
        }
        return set_card;

    }

    public static void main(String[] args) throws IOException{
        Request req = new Request("register", "Val");
        int token = serverRequest(req).token;
        System.out.println("Your token: " + token);
        req = new Request("fetch_cards", token);
        ArrayList<Card> cards = new ArrayList<>();
        cards = serverRequest(req).cards;
        //System.out.println(cards);




        boolean flag = true;
        while (flag) {
            ArrayList<Card> set = findSet(cards);
            if (set.size()==0){
                break;
            }
            req = new Request("take_set",token, set);
            Response res = serverRequest(req);
            System.out.println("Cards left: "+res.cards_left + ", point: " + res.points);
            for (int i = 0; i < set.size(); i++) {
                cards.remove(set.get(i));
            }
            if (cards.size()<3){
                flag = false;
            }
        }

// 3)
        // зарегистрировться на сервере и получить токен
        // получить список карт с сервера
        // найти среди 12 карт 3 составляющие сет
        // отправить запрос с 3 картами (сетом) на сервер
        // вывести ответ сервера (число очков)
        // можно повторять запросы, пока есть сеты
    }
}
