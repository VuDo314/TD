package com.example.towerdefense40;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Point;
import android.os.Build;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

import androidx.annotation.RequiresApi;

import java.util.ArrayList;
import java.util.function.Predicate;

class Renderer {
    private int S;
    private Paint paint;
    private Canvas canvas;
    private SurfaceHolder surfaceHolder;
    private Map map;
    private ArrayList<Enemy>enemies;
    Renderer(SurfaceView sv, Point size){
        paint = new Paint();
        surfaceHolder = sv.getHolder();
        S = CONSTANT.SQUARE_SIZE;
        map = new Map(sv.getContext(), size);
        enemies = new ArrayList<>();
        for(int i=0; i < CONSTANT.WAVE1_ENEMY;i++){
            Enemy g = new Enemy(sv.getContext(), size);
            enemies.add(g);
        }
        for(int i = 0; i< enemies.size(); i++) {
            enemies.get(i).setLocation(enemies.get(i).getSquareSize(), enemies.get(i).getSquareSize()*13);
        }
        boolean touched = false;
    }
    @RequiresApi(api = Build.VERSION_CODES.N)
    void draw(HUD hud, GameState gameState, UIController uiController){
        if(surfaceHolder.getSurface().isValid()){
            this.canvas = surfaceHolder.lockCanvas();
            canvas.drawColor(Color.argb(255,0,0,0));
            gameState.startDrawing();
            if(gameState.getDrawing()) {
                map.draw(canvas, paint);
                uiController.getT().draw(canvas,paint);

                uiController.getT2().draw(canvas,paint);
                if(gameState.getTime()>=gameState.getTimeToSpawn()) {
                    gameObjectSpawn(enemies, gameState);
                    if(uiController.getT().distance(enemies.get(0))<=CONSTANT.FIRING_RANGE) {
                        uiController.getT().getProjectile().draw(canvas, paint);
                        if(gameState.getPaused()){
                            uiController.getT().getProjectile().pause();
                        }else {
                            uiController.getT().getProjectile().reset();
                            uiController.getT().getProjectile().move(uiController.getT());
                            Predicate<Enemy> enemyPredicate = enemy -> enemy.getLocationX()==uiController.getT().getProjectile().getLocationX();
                            enemies.removeIf(enemyPredicate);//change module setting source and language to level 8
                        }
                    }
                }
            }
            hud.draw(canvas, paint, gameState);
            surfaceHolder.unlockCanvasAndPost(canvas);
        }
    }
    private void arrowSpawn(){

    }
    private void gameObjectSpawn(ArrayList<Enemy> enemies, GameState gameState) {
        int i=enemies.size()-1;
        enemies.get(i).draw(canvas, paint);
        if(gameState.getPaused()){
            enemies.get(i).pause();
        }else {
            if (enemies.get(i).Dead()) {
                enemies.get(i).pause();
            } else {
                enemies.get(i).resume();
                enemies.get(i).move();
            }
        }
        while (enemies.get(i).getLocationX() >= 2*CONSTANT.SQUARE_SIZE) {
            i=i-1;
            enemies.get(i).draw(canvas, paint);
            if(gameState.getPaused()){
                enemies.get(i).pause();
            }else {
                if (enemies.get(i).Dead()) {
                    enemies.get(i).pause();
                } else {
                    enemies.get(i).resume();
                    enemies.get(i).move();
                }
            }
            if(i<=0) break;
        }
    }
}