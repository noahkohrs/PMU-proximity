package com.inc.pmu.models;

public class Card {
    public static final int MIN_NUMBER = 1 ;
    public static final int MAX_NUMBER = 13 ;

    public final Suit suit ;
    public final int number;

    public Card(Suit suit, int number) {
        this.suit = suit;
        this.number = number;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (obj == this) {
            return true;
        }
        if (obj.getClass() != this.getClass()) {
            return false;
        }
        Card card = (Card) obj;
        return card.suit == this.suit && card.number == this.number;
    }


}
