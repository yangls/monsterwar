package com.mw.actor;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.GridPoint2;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.FloatArray;
import com.mw.utils.Dungeon;

/**
 * Created by BanditCat on 2016/3/29.
 */
public class MapShadow extends Actor{
    private OrthographicCamera camera;
    private ShapeRenderer shapeRenderer;
    private GridPoint2 sightPosIndex = new GridPoint2(0,0);
    private int width = 0,height = 0;
    private int sightRadius = 6;
    private Array<EdgeLine> lines = new Array<EdgeLine>();
    private int[][] dungeonArray;
    //视野多边形
    private FloatArray floatArray = new FloatArray();

    public boolean isChangedPos = false;

    private Rectangle sightRectangle = new Rectangle(0,0,0,0);

    private Pixmap pixmap;
    private Texture texture;

    private float sx_old=-1,sy_old=-1;int r_old = -1;



    public MapShadow(OrthographicCamera camera,int width,int height,int[][] dungeonArray) {
        this.dungeonArray = dungeonArray;
        this.camera = camera;
        this.height = height;
        this.width = width;
        shapeRenderer = new ShapeRenderer();

        // Create an empty dynamic pixmap
        pixmap = new Pixmap(width, height, Pixmap.Format.RGBA8888); // Pixmap.Format.RGBA8888);
        // Create a texture to contain the pixmap
        texture = new Texture(width, height, Pixmap.Format.RGBA8888); // Pixmap.Format.RGBA8888);
        texture.setFilter(Texture.TextureFilter.Nearest, Texture.TextureFilter.Linear);
        texture.setWrap(Texture.TextureWrap.ClampToEdge, Texture.TextureWrap.ClampToEdge);
        pixmap.setColor(new Color(0,0,0,1f));
        pixmap.fillRectangle(0,0,width,height);
        texture.draw(pixmap, 0, 0);
    }

    @Override
    public void act(float delta) {
        super.act(delta);
    }

    @Override
    public void draw(Batch batch, float parentAlpha) {
        //混合模式
        Gdx.gl.glEnable(GL20.GL_BLEND);
        Gdx.gl.glBlendFunc(GL20.GL_SRC_ALPHA,GL20.GL_ONE_MINUS_SRC_ALPHA);
        batch.setProjectionMatrix(camera.combined);
        batch.draw(texture,0,0);
        super.draw(batch, parentAlpha);
//        drawShadowShape();
        Gdx.gl.glDisable(GL20.GL_BLEND);

    }
    private void drawShadowShape(){
        shapeRenderer.setProjectionMatrix(camera.combined);
        if(shapeRenderer.isDrawing()){
            return;
        }
//        //画视野
//        float sightX = (sightPosIndex.x - sightRadius)*32;
//        float sightY = (sightPosIndex.y - sightRadius)*32;
//        float sightWidth = (sightRadius*2+1)*32;
//        float sightHeight = (sightRadius*2+1)*32;
//        shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);
//        shapeRenderer.setColor(new Color(0,0,0,0.9f));
//        shapeRenderer.rect(sightX,sightY,sightWidth,sightHeight);
//        //画阴影
//        shapeRenderer.setColor(new Color(0,0,0,0.9f));
//        shapeRenderer.rect(0,0,sightX,height);
//        shapeRenderer.rect(sightX,0,sightWidth,sightY);
//        shapeRenderer.rect(sightX+sightWidth,0,width-sightX-sightWidth,height);
//        shapeRenderer.rect(sightX,sightY+sightHeight,sightWidth,height-sightY-sightHeight);

        float sx = (sightPosIndex.x*32)+16;//视野的横坐标
        float sy = (sightPosIndex.y*32)+16;//视野的纵坐标
        float[] arr = floatArray.toArray();

        shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);
        shapeRenderer.setColor(new Color(0,0,0,0.9f));
        shapeRenderer.rect(0,0,width,height);
        shapeRenderer.end();

        shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);
        shapeRenderer.setColor(new Color(0,1,0,0f));
        for(int i = 0;i+3< arr.length;i+=2){
            shapeRenderer.triangle(sx,sy,arr[i],arr[i+1],arr[i+2],arr[i+3]);
        }
        shapeRenderer.end();


        shapeRenderer.begin(ShapeRenderer.ShapeType.Line);
        shapeRenderer.setColor(Color.PINK);
        if(arr.length>0){
            shapeRenderer.polygon(arr);
        }
        shapeRenderer.end();
    }
    private void drawShadow() {
        float[] arr = floatArray.toArray();
        float sx = (sightPosIndex.x*32)+16;//视野的横坐标
        float sy = (sightPosIndex.y*32)+16;//视野的纵坐标
        int sightX = (sightPosIndex.x - sightRadius)*32;
        int sightY = (sightPosIndex.y - sightRadius)*32;
        int sightWidth = (sightRadius*2+1)*32;
        int sightHeight = (sightRadius*2+1)*32;
        Pixmap.setBlending(Pixmap.Blending.None);
        pixmap.setColor(new Color(0,0,0,0.6f));
//        pixmap.fillCircle((int)sx,(int)(height-sy),(sightRadius+1)*32);
//        pixmap.fillRectangle((int)sx-sightWidth/2,(int)(height-sy)-sightHeight/2,sightWidth,sightHeight);
        int r = (int)((sightRectangle.width>sightRectangle.height?sightRectangle.width/2+32:sightRectangle.height/2+32)*1.414);
        pixmap.fillCircle((int)sx,(int)(height-sy),r);
        if(sx_old != -1 && sy_old != -1&&r_old!=-1){
            pixmap.fillCircle((int)sx_old,(int)(height-sy_old),r_old);
        }
        //坐标系y是反过来的
        pixmap.setColor(new Color(0,0,0,0.3f));
        for(int i = 0;i+3< arr.length;i+=2){
            pixmap.fillTriangle((int)sx,(int)(height-sy),(int)arr[i],(int)(height-arr[i+1]),(int)arr[i+2],(int)(height-arr[i+3]));
        }
        sx_old = sx;sy_old = sy;
        r_old = r;
        texture.draw(pixmap,0,0);

    }

    private void upDateShadowLines(){
        if(lines.size>0){
            EdgeLine ed = lines.get(0);
            int next = ed.getNext();
            floatArray.clear();
            while (next >= 0){
                if(isChangedPos){
                    Gdx.app.log("next",next+"");
                }
                EdgeLine en = lines.get(next);
                floatArray.add(ed.getEnd().x);
                floatArray.add(ed.getEnd().y);
                floatArray.add(en.getStart().x);
                floatArray.add(en.getStart().y);
                floatArray.add(en.getEnd().x);
                floatArray.add(en.getEnd().y);
                if(next == 0){
                    break;
                }
                next = en.getNext();
                ed = en;
            }
            isChangedPos = false;
            Vector2
            lbY=new Vector2(width,0),rbY=new Vector2(0,0)
            ,ltY=new Vector2(width,0),rtY=new Vector2(0,0)
            ,ltX=new Vector2(0,0),lbX=new Vector2(0,height)
            ,rtX=new Vector2(0,0),rbX=new Vector2(0,height);
            float maxx=0,maxy=0,minx=width,miny=height;
            for (int i = 0; i+1 < floatArray.size; i+=2) {
                float x = floatArray.get(i);
                float y = floatArray.get(i+1);
                if(x >= maxx){//最大x值的y轴，轴上有rtX,rbX
                    maxx = x;
                    rbX.x = x;
                    if(y<=rbX.y){
                        rbX.y = y;
                    }
                    rtX.x = x;
                    if(y>=rtX.y){
                        rtX.y = y;
                    }
                }
                if(y >= maxy){//最大y值的x轴，轴上有ltY,rtY
                    maxy = y;
                    ltY.y = y;
                    if(x <= ltY.x){
                        ltY.x = x;
                    }
                    rtY.y= y;
                    if(x >= rtY.x){
                        rtY.x = x;
                    }
                }
                if(x <= minx){//最小x值的y轴，轴上有ltX,lbX
                    minx = x;
                    ltX.x = x;
                    if(y >= ltX.y){
                        ltX.y = y;
                    }
                    lbX.x = x;
                    if(y <= lbX.y){
                        lbX.y = y;
                    }
                }
                if(y <= miny){//最小y值的x轴，轴上有lbY,rbY
                    miny = y;
                    lbY.y = y;
                    if(x <= lbY.x){
                        lbY.x = x;
                    }
                    rbY.y = y;
                    if(x >= rbY.x){
                        rbY.x = x;
                    }
                }
            }
            sightRectangle.x = minx;
            sightRectangle.y = miny;
            sightRectangle.width = maxx-minx;
            sightRectangle.height = maxy-miny;
        }
    }

    public GridPoint2 getSightPosIndex() {
        return sightPosIndex;
    }

    public void dispose(){
        shapeRenderer.dispose();
        texture.dispose();
        pixmap.dispose();
    }

    private EdgeLine getEdgeLine(int x,int y,int x1,int y1){
        int id = lines.size -1;//按加入位置创建id
        if(id < 0){
            id = 0;
        }
        float sx = (sightPosIndex.x*32)+16;//视野的横坐标
        float sy = (sightPosIndex.y*32)+16;//视野的纵坐标
        EdgeLine edgeLine = new EdgeLine();
        edgeLine.getStart().x = x;
        edgeLine.getStart().y = y;
        edgeLine.getEnd().x = x1;
        edgeLine.getEnd().y = y1;
        float dx = x+(x1-x)/2;
        float dy = y+(y1-y)/2;
        edgeLine.setDistance(Vector2.dst(sx,sy,dx,dy));
        edgeLine.setId(id);
        return edgeLine;
    }

    private boolean isBlock(int i,int j){
        return !(dungeonArray[i][j] != Dungeon.tileStoneWall
                &&dungeonArray[i][j]!= Dungeon.tileDirtWall
                &&dungeonArray[i][j]!= Dungeon.tileUnused);
    }

    public void updateLines(){
        lines.clear();
        int sx = (sightPosIndex.x*32)+16;//视野的横坐标
        int sy = (sightPosIndex.y*32)+16;//视野的纵坐标
        int x = 0;//tile左下角横坐标
        int y = 0;//tile左下角纵坐标

        int x1 = 0;//tile左上角横坐标
        int y1 = 0;//tile左上角纵坐标

        int x2 = 0;//tile右上角横坐标
        int y2 = 0;//tile右上角纵坐标

        int x3 = 0;//tile右下角横坐标
        int y3 = 0;//tile右下角纵坐标
        int iMin = sightPosIndex.x - sightRadius;
        if(iMin < 0){
            iMin = 0;
        }
        int iMax = sightPosIndex.x + sightRadius+1;
        if(iMax > 32){
            iMax = 32;
        }
        int jMin = sightPosIndex.y - sightRadius;
        if(jMin < 0){
            jMin = 0;
        }
        int jMax = sightPosIndex.y + sightRadius+1;
        if(jMax > 32){
            jMax = 32;
        }
        for (int i = iMin; i < iMax; i++) {
            for (int j = jMin; j < jMax; j++) {
                x = i*32;
                y = j*32;
                x1 = x;
                y1 = y+1*32;
                x2 = x+1*32;
                y2 = y+1*32;
                x3 = x+1*32;
                y3 = y;
                //添加边围住视野
                if(j==jMin&&i!=iMax-1&&i!=iMin&&!isBlock(i,j+1)){//中心点下层
                    lines.add(getEdgeLine(x1,y1,x2,y2));
                }
                if(i==iMin&&j!=jMax-1&&j!=jMin&&!isBlock(i+1,j)){//中心点左层
                    lines.add(getEdgeLine(x2,y2,x3,y3));
                }
                if(j==jMax-1&&i!=iMax-1&&i!=iMin&&!isBlock(i,j-1)){//中心点上层
                    lines.add(getEdgeLine(x3,y3,x,y));
                }
                if(i==iMax-1&&j!=jMax-1&&j!=jMin&&!isBlock(i-1,j)){//中心点右层
                    lines.add(getEdgeLine(x,y,x1,y1));
                }
                if(i==iMin||j==jMin||i==iMax-1||j==jMax-1){
                    continue;//如果是边就跳过
                }
                if(!isBlock(i,j)){
                    continue;//如果不是视野障碍物就跳过
                }

                if(sy >= y1&&!isBlock(i,j+1)){
                    //top
                    lines.add(getEdgeLine(x1,y1,x2,y2));
                }
                if(sy <= y&&!isBlock(i,j-1)){
                    //bottom
                    lines.add(getEdgeLine(x3,y3,x,y));
                }
                if(sx <= x&&!isBlock(i-1,j)){
                    //left
                    lines.add(getEdgeLine(x,y,x1,y1));
                }
                if(sx >= x2&&!isBlock(i+1,j)){
                    //right
                    lines.add(getEdgeLine(x2,y2,x3,y3));
                }

            }
        }
        connectEdges();
        calculateProjections();
        upDateShadowLines();
        drawShadow();

    }


    private void connectEdges(){
        //按距离从近到远排序
        lines.sort();
        for (int i=0; i<this.lines.size; i++) {
            EdgeLine eNow = lines.get(i);
            if (eNow.getPrev() != -1 && eNow.getNext() != -1) {
                continue;
            }
            for(int j=0; j<lines.size; j++) {
                if (i == j) {
                    continue;
                }
                EdgeLine eCheck = lines.get(j);
                if (eCheck.getPrev() != -1 && eCheck.getNext() != -1) {
                    continue;
                }
                //如果一条边的头是另一条边的尾，那么这条边的prev是另一条，另一条的next是这一条
                if (eNow.getEnd().x == eCheck.getStart().x && eNow.getEnd().y == eCheck.getStart().y) {
                    eNow.setNext(j);;
                    eCheck.setPrev(i);
                }
            }
        }
    }
    private void calculateProjections(){
        int sx = (sightPosIndex.x*32)+16;//视野的横坐标
        int sy = (sightPosIndex.y*32)+16;//视野的纵坐标
        Vector2 lightSource = new Vector2(sx,sy);
        // Start from the beginning to project lines
        for(int i = 0;i<lines.size;i++){
            EdgeLine e = lines.get(i);
            // Find not connected point for next
            if(e.getNext() == -1){
                float[]abc = getLineABC(e.getEnd(),lightSource);
                float[]intersectionData = checkIntersection(abc,e.getEnd(),i);
                // if found intersection point then split the edge at intersection point
                if (intersectionData[2] != -1) {
                    updateEdge(i, (int)intersectionData[2],new Vector2(intersectionData[0],intersectionData[1]), true);
                }
            }
            // Find not connected point for prev
            if (e.getPrev() == -1) {
                float[]abc = getLineABC(e.getEnd(),lightSource);
                abc = this.getLineABC(e.getStart(), lightSource);
                float[]intersectionData = checkIntersection(abc,e.getStart(),i);
                // if found intersection point then split the edge at intersection point
                if (intersectionData[2] != -1) {
                    this.updateEdge(i, (int)intersectionData[2],new Vector2(intersectionData[0],intersectionData[1]), false);
                }
            }

        }
    }

    private void updateEdge(int edgeID, int targetEdgeID, Vector2 p, boolean isNext) {
        // The edge that start the projection
        EdgeLine edgeStart = lines.get(edgeID);
        // The target edge
        EdgeLine edgeToBeSliced = lines.get(targetEdgeID);

        // Calculate for the edge to be kept
        if (isNext) {
            edgeStart.setNext(targetEdgeID);
            edgeToBeSliced.setStart(p);
            edgeToBeSliced.setPrev(edgeID);
        } else {
            edgeStart.setPrev(targetEdgeID);
            edgeToBeSliced.setEnd(p);
            edgeToBeSliced.setNext(edgeID);
        }

        // Update all the 3 edges
        lines.set(edgeID,edgeStart);
        lines.set(targetEdgeID,edgeToBeSliced);
    }

    private float[] getLineABC(Vector2 pt1, Vector2 pt2){
        float[] abc = {0,0,0};
        if((pt1.y==pt2.y)&&(pt1.x==pt2.x)){
            abc[0]=0;abc[1]=0;abc[2]=0;
        }else if(pt1.x == pt2.x){
            abc[0]=1;abc[1]=0;abc[2]=-pt1.x;
        }else {
            abc[0]=-(pt2.y - pt1.y) / (pt2.x - pt1.x);abc[1]=1;abc[2]=pt1.x * (pt2.y - pt1.y) / (pt2.x - pt1.x) - pt1.y;
        }
        return abc;
    }
   private Vector2 getIntersectionPoint(float[]abc1, float[]abc2) {
        Vector2 p = new Vector2(0,0);
        float x = 0,y = 0;
        float a1 = abc1[0], b1 = abc1[1], c1 = abc1[2],
                a2 = abc2[0], b2 = abc2[1], c2 = abc2[2];

        if ((b1 == 0) && (b2 == 0)) {
            return p;
        } else if (b1 == 0) {
            x = -c1;
            y = -(a2 * x + c2) / b2;
        } else if (b2 == 0) {
            x = -c2;
            y = -(a1 * x + c1) / b1;
        } else {
            if ((a1 / b1) == (a2 / b1)) {
                return p;
            } else {
                x = (c1 - c2) / (a2  - a1);
                y = -(a1 * x) - c1;
            }
        }
       p.x = x;p.y=y;
        return p;
    }
    private float[] checkIntersection(float[] lineABC,Vector2 point,float currentID){
        boolean found = false;
        int sx = (sightPosIndex.x*32)+16;//视野的横坐标
        int sy = (sightPosIndex.y*32)+16;//视野的纵坐标
        Vector2 lightSource = new Vector2(sx,sy);
        Vector2 p = new Vector2(0,0);
        int i;

        for (i=0; i<lines.size; i++) {
            // Skip current point
            if (i != currentID) {
                EdgeLine edge = lines.get(i);

               float[] abc = this.getLineABC(edge.getStart(), edge.getEnd());
                p = this.getIntersectionPoint(abc, lineABC);

                if ((p.x == point.x) && (p.y == point.y))   continue;   // Skip current point, confirm

                // check direction, intersections in the middle will be ignored
                if ((lightSource.x > point.x) && (p.x > point.x))   continue;
                if ((lightSource.x < point.x) && (p.x < point.x))   continue;
                if ((lightSource.y > point.y) && (p.y > point.y))   continue;
                if ((lightSource.y < point.y) && (p.y < point.y))   continue;

                // check if the intersection point is not on the edge
                float bigX, bigY, smallX, smallY;
                if (edge.getStart().x > edge.getEnd().x) {
                    bigX = edge.getStart().x;       smallX = edge.getEnd().x;
                } else {
                    bigX = edge.getEnd().x;       smallX = edge.getStart().x;
                }

                if (edge.getStart().y > edge.getEnd().y) {
                    bigY = edge.getStart().y;       smallY = edge.getEnd().y;
                } else {
                    bigY = edge.getEnd().y;       smallY = edge.getStart().y;
                }

                // If the intersection point is note on the edge, ignore it
                if ((p.x < smallX) || (p.x > bigX) || (p.y < smallY) || (p.y > bigY))
                    continue;

                found = true;
                break;

            } // end if

        } // end for

        // if not found, marked as not found with zero filled
        if (!found) {
            p.x =0;p.y=0;
            i = -1;
        }

        // return intersection point and intersect id
        return new float[]{ p.x, p.y, i};
    }

}
