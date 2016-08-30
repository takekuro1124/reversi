package com.example.denki.haribotereversi;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.view.MotionEvent;
import android.view.View;

/**
 * Created by denki on 2016/08/30.
 */
class ReversiView extends View {
    private Paint paint = new Paint();

    private Resources res=this.getContext().getResources();
    private final Bitmap JPG_BOARD=BitmapFactory.decodeResource(res,R.drawable.board);
    private final Bitmap JPG_BLACK=BitmapFactory.decodeResource(res,R.drawable.black);
    private final Bitmap JPG_WHITE=BitmapFactory.decodeResource(res,R.drawable.white);
    private final Bitmap JPG_LIGHT=BitmapFactory.decodeResource(res,R.drawable.light);
    private final Bitmap JPG_TITLE=BitmapFactory.decodeResource(res,R.drawable.title);

    private final int TITLE=0;
    private final int PLAYER=1;
    private final int COM = 2;
    private final int TURN=3;
    private final int REVERS=4;
    private final int CONTROL=5;
    private final int PASS=6;
    private final int RESULT=7;
    private final int[] MOVE={-11,-10,-9,-1,1,9,10,11};
    private final int BLACK=0;
    private final int WHITE=1;

    private int[] board = new int[100];
    private int page = TITLE;
    private int turn;
    private int[] placeMap=new int[100];
    private int playerColor;
    private int place;

    public ReversiView(Context context){
        super(context);
        paint.setARGB(200,255,255,255);
        paint.setTextSize(30);
    }

    @Override
    public void onDraw(Canvas c){
        int i;

        c.drawBitmap(JPG_BOARD,0,0,paint);
        for(i=11;i<88;i++) {
            if (playerColor == BLACK) {
                if (board[i] == PLAYER)
                    c.drawBitmap(JPG_BLACK, 48 * (i % 10), 48 * (i / 10), paint);
                if (board[i] == COM) c.drawBitmap(JPG_WHITE, 48 * (i % 10), 48 * (i / 10), paint);
            } else {
                if (board[i] == PLAYER)
                    c.drawBitmap(JPG_BLACK, 48 * (i % 10), 48 * (i / 10), paint);
                if (board[i] == COM) c.drawBitmap(JPG_WHITE, 48 * (i % 10), 48 * (i / 10), paint);
            }
        }

        switch (page){
            case TITLE:
                c.drawBitmap(JPG_TITLE,0,0,paint);
                break;
            case TURN:

                page=turn;
                invalidate();
                break;
            case PLAYER:
                makePlaceMap(PLAYER);

                for(i=11;i<=88;i++){
                    if(placeMap[i]>0) c.drawBitmap(JPG_LIGHT,48*(i%10),48*(i/10),paint);
                }
                break;
            case COM:
                makePlaceMap(COM);
                for(i=11;i<=88;i++){
                    if(placeMap[i]>0) c.drawBitmap(JPG_LIGHT,48*(i%10),48*(i/10),paint);
                }
                break;
            case REVERS:

                reverse(turn,place);

                page=CONTROL;
                invalidate();
                break;
            case CONTROL:

                if(turn==PLAYER)turn = COM;
                else turn = PLAYER;

                if (makePlaceMap(PLAYER)==true&&makePlaceMap(COM)==true)page=RESULT;
                else if (makePlaceMap(turn)==true)page =PASS;
                else page = TURN;

                page=TURN;
                invalidate();
                break;
            case PASS:

                c.drawText("パス",200,600,paint);

                if (turn==PLAYER)turn=COM;
                else turn=PLAYER;

                page = TURN;
                invalidate();
                break;
            case RESULT:
                c.drawText("結果",200,550,paint);
                c.drawLine(50,560,430,560,paint);
                c.drawText("黒　"+count(BLACK)+"　個",100,650,paint);
                c.drawText("白　"+count(WHITE)+"　個",100,700,paint);
                break;
        }
    }

    public boolean onTouchEvent(MotionEvent me){

        int i;

        int padX=(int)(me.getRawX()/48);
        int padY=(int)(me.getRawY()/48);

        if (me.getAction()==MotionEvent.ACTION_DOWN){
            switch (page){
                case TITLE:

                    for (i=0;i<100;i++) board[i]=0;
                    for (i=0;i<10;i++) board[i]=-1;
                    for (i=0;i<9;i++) board[i*10]=-1;
                    for (i=0;i<9;i++) board[i*10+9]=-1;
                    for (i=0;i<10;i++) board[i+90]=-1;

                    if(2<=padX&&padX<=3&&7<=padY&&padY<=8){
                        playerColor=BLACK;
                        board[44]=COM;
                        board[45]=PLAYER;
                        board[54]=PLAYER;
                        board[55]=COM;
                        turn=PLAYER;
                        makePlaceMap(turn);

                        page=TURN;
                        invalidate();
                    }
                    if(6<=padX&&padX<=7&&7<=padY&&padY<=8){
                        playerColor=WHITE;
                        board[44]=COM;
                        board[45]=PLAYER;
                        board[54]=PLAYER;
                        board[55]=COM;
                        turn=PLAYER;
                        makePlaceMap(turn);

                        page=TURN;
                        invalidate();
                    }
                    board[44]=COM;
                    board[45]=PLAYER;
                    board[54]=PLAYER;
                    board[55]=COM;
                    page = TURN;
                    turn=PLAYER;
                    invalidate();
                    break;
                case PLAYER:
                    if(placeMap[padX+padY*10]>0) {

                        place = padX + padY * 10;
                        page = REVERS;
                        invalidate();
                    }
                    break;
                case COM:
                    if(placeMap[padX+padY*10]>0) {

                        place = padX + padY * 10;
                        page = REVERS;
                        invalidate();
                    }
                    break;
                case PASS:
                    break;
                case RESULT:

                    page =TITLE;
                    invalidate();
                    break;
            }
        }
        return  true;
    }

    void reverse(int myCoin,int p){
        int yourCoin=PLAYER;
        int i,j,k;

        if(myCoin==PLAYER)yourCoin=COM;

        board[p]=myCoin;
        for (i=0;i<8;i++){
            if (board[p+MOVE[i]]==yourCoin){
                for(j=2;j<8;j++){
                    if(board[p+MOVE[i]*j]==myCoin){
                        for(k=1;k<j;k++){
                            board[p+MOVE[i]*k]=myCoin;
                        }
                        break;
                    }else if (board[p+MOVE[i]*j]==yourCoin) {
                    }else {
                        break;
                    }
                }
            }
        }
    }
    public boolean makePlaceMap(int myCoin){
        int yourCoin= PLAYER;
        int i,j;
        boolean pass=true;

        if (myCoin==PLAYER) yourCoin=COM;

        for (int p=0;p<100;p++){
            placeMap[p]=0;
            if(0<p&&p<100&&board[p]==0){
                for(i=0;i<8;i++){
                    if(board[p+MOVE[i]]==yourCoin){
                        for (j=2;j<8;j++){
                            if(board[p+MOVE[i]*j]==myCoin) {
                                placeMap[p] += j - 1;
                                pass = false;
                                break;
                            }else if(board[p+MOVE[i]*j]==yourCoin) {
                            }else {
                                break;
                            }
                        }
                    }
                }
            }
        }
        return pass;
    }

    public  int count(int color){
        int count=0;

        if(playerColor==color) {
            for (int i = 0; i < 100; i++) {
                if (board[i] == PLAYER) count++;
            }
        }else{
            for(int i=0;i<100;i++){
                if(board[i]==COM) count++;
            }
        }
        return count;
    }
}




