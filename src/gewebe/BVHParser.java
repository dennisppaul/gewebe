package gewebe;

/* source from from https://github.com/kearnie/beepboop_mocap */

import processing.core.PApplet;
import processing.core.PMatrix3D;
import processing.core.PVector;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

public class BVHParser {

    private final List<BVHBone> _bones;
    private BVHBone _currentBone;
    private int _currentFrame = 0;
    private int _currentLine;
    private float _frameTime;
    private List<List<Float>> _frames;
    private List<BVHLine> _lines;
    private boolean _motionLoop;
    private int _nbFrames;
    private BVHBone _rootBone;

    public BVHParser() {
        _bones = new ArrayList<>();
        _motionLoop = true;
    }

    /**
     * if set to True motion will loop at end
     */
    public boolean getMotionLoop() {
        return _motionLoop;
    }

    /**
     * set Loop state
     * @param value
     */
    public void setMotionLoop(boolean value) {
        _motionLoop = value;
    }

    /**
     * to string
     * @return
     */
    public String toStr() {
        return _rootBone.structureToString();
    }

    /**
     * get frame total
     *
     * @return
     */
    public int getNbFrames() {
        return _nbFrames;
    }

    /**
     * get bones list
     *
     * @return
     */
    public List<BVHBone> getBones() {
        return _bones;
    }

    /**
     * go to the frame at index
     */
    public void moveFrameTo(int __index) {
        if (!_motionLoop) {
            if (__index >= _nbFrames) {
                _currentFrame = _nbFrames - 1;//last frame
            }
        } else {
            while (__index >= _nbFrames) {
                __index -= _nbFrames;
            }
            _currentFrame = __index; //looped frame
        }
        _updateFrame();
    }

    /**
     * go to millisecond of the BVH
     *
     * @param mills millisecond
     */
    public void moveMsTo(int mills) {
        float frameTime = _frameTime * 1000;
        int curFrame = (int) (mills / frameTime);
        moveFrameTo(curFrame);
    }

    /**
     * update bone position and rotation
     */
    public void update() {
        update(getBones().get(0));
    }

    public void parse(String[] srces) {
        String[] linesStr = srces;
        // liste de BvhLines
        _lines = new ArrayList<BVHLine>();

        for (String lineStr : linesStr) {
            _lines.add(new BVHLine(lineStr));
        }

        _currentLine = 1;
        _rootBone = _parseBone();

        // center locs
        //_rootBone.offsetX = _rootBone.offsetY = _rootBone.offsetZ = 0;

        _parseFrames();
    }

    protected void update(BVHBone bone) {

        PMatrix3D m = new PMatrix3D();

        m.translate(bone.getXposition(), bone.getYposition(), bone.getZposition());
        m.translate(bone.getOffsetX(), bone.getOffsetY(), bone.getOffsetZ());

        m.rotateY(PApplet.radians(bone.getYrotation()));
        m.rotateX(PApplet.radians(bone.getXrotation()));
        m.rotateZ(PApplet.radians(bone.getZrotation()));

        bone.global_matrix = m;

        if (bone.getParent() != null && bone.getParent().global_matrix != null) {
            m.preApply(bone.getParent().global_matrix);
        }
        m.mult(new PVector(), bone.getAbsPosition());

        if (bone.getChildren().size() > 0) {
            for (BVHBone child : bone.getChildren()) {
                update(child);
            }
        } else {
            m.translate(bone.getEndOffsetX(), bone.getEndOffsetY(), bone.getEndOffsetZ());
            m.mult(new PVector(), bone.getAbsEndPosition());
        }
    }

    private void _updateFrame() {
        if (_currentFrame >= _frames.size()) {
            return;
        }
        List<Float> frame = _frames.get(_currentFrame);
        int count = 0;
        for (float n : frame) {
            BVHBone bone = _getBoneInFrameAt(count);
            String prop = _getBonePropInFrameAt(count);
            if (bone != null) {
                Method getterMethod;
                try {
                    getterMethod = bone.getClass().getDeclaredMethod("set".concat(prop), new Class[]{float.class});
                    getterMethod.invoke(bone, n);
                } catch (SecurityException e) {
                    e.printStackTrace();
                    System.err.println("ERROR WHILST GETTING FRAME - 1");
                } catch (NoSuchMethodException e) {
                    e.printStackTrace();
                    System.err.println("ERROR WHILST GETTING FRAME - 2");
                } catch (IllegalArgumentException e) {
                    e.printStackTrace();
                    System.err.println("ERROR WHILST GETTING FRAME - 3");
                } catch (IllegalAccessException e) {
                    e.printStackTrace();
                    System.err.println("ERROR WHILST GETTING FRAME - 4");
                } catch (InvocationTargetException e) {
                    e.printStackTrace();
                    System.err.println("ERROR WHILST GETTING FRAME - 5");
                }
            }
            count++;
        }
    }

    private String _getBonePropInFrameAt(int n) {
        int c = 0;
        for (BVHBone bone : _bones) {
            if (c + bone.getNbChannels() > n) {
                n -= c;
                return bone.getChannels().get(n);
            } else {
                c += bone.getNbChannels();
            }
        }
        return null;
    }

    private BVHBone _getBoneInFrameAt(int n) {
        int c = 0;
        for (BVHBone bone : _bones) {
            c += bone.getNbChannels();
            if (c > n) {
                return bone;
            }
        }
        return null;
    }

    private void _parseFrames() {
        int currentLine = _currentLine;
        for (; currentLine < _lines.size(); currentLine++) {
            if (_lines.get(currentLine).getLineType() == BVHLine.MOTION) {
                break;
            }
        }

        if (_lines.size() > currentLine) {
            currentLine++; //Frames
            _nbFrames = _lines.get(currentLine).getNbFrames();
            currentLine++; //FrameTime
            _frameTime = _lines.get(currentLine).getFrameTime();
            currentLine++;

            _frames = new ArrayList<List<Float>>();
            for (; currentLine < _lines.size(); currentLine++) {
                _frames.add(_lines.get(currentLine).getFrames());
            }
        }
    }

    private BVHBone _parseBone() {
        //_currentBone is Parent
        BVHBone bone = new BVHBone(_currentBone);

        _bones.add(bone);

        bone.setName(_lines.get(_currentLine)._boneName); //1

        // +2 OFFSET
        _currentLine++; // 2 {
        _currentLine++; // 3 OFFSET
        bone.setOffsetX(_lines.get(_currentLine).getOffsetX());
        bone.setOffsetY(_lines.get(_currentLine).getOffsetY());
        bone.setOffsetZ(_lines.get(_currentLine).getOffsetZ());

        // +3 CHANNELS
        _currentLine++;
        bone.setnbChannels(_lines.get(_currentLine).getNbChannels());
        bone.setChannels(_lines.get(_currentLine).getChannelsProps());

        // +4 JOINT or End Site or }
        _currentLine++;
        while (_currentLine < _lines.size()) {
            String lineType = _lines.get(_currentLine).getLineType();
            if (BVHLine.BONE.equals(lineType)) //JOINT or ROOT
            {
                BVHBone child = _parseBone(); //generate new BvhBONE
                child.setParent(bone);
                bone.getChildren().add(child);
            } else if (BVHLine.END_SITE.equals(lineType)) {
                _currentLine++; // {
                _currentLine++; // OFFSET
                bone.setEndOffsetX(_lines.get(_currentLine).getOffsetX());
                bone.setEndOffsetY(_lines.get(_currentLine).getOffsetY());
                bone.setEndOffsetZ(_lines.get(_currentLine).getOffsetZ());
                _currentLine++; //}
                _currentLine++; //}
                return bone;
            } else if (BVHLine.BRACE_CLOSED.equals(lineType)) {
                return bone; //}
            }
            _currentLine++;
        }
        System.out.println("//Something strage");
        return bone;
    }
}
