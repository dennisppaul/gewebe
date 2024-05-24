/*
 * Gewebe
 *
 * This file is part of the *wellen* library (https://github.com/dennisppaul/wellen).
 * Copyright (c) 2024 Dennis P Paul.
 *
 * This library is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, version 3.
 *
 * This library is distributed in the hope that it will be useful, but
 * WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program. If not, see <http://www.gnu.org/licenses/>.
 */

package gewebe;

/* source from from https://github.com/kearnie/beepboop_mocap */

import java.util.ArrayList;
import java.util.List;

public class BVHLine {

    public static final String HIERARCHY    = "HIERARCHY";
    public static final String BONE         = "BONE";
    public static final String BRACE_OPEN   = "BRACE_OPEN";
    public static final String BRACE_CLOSED = "BRACE_CLOSED";
    public static final String OFFSET       = "OFFSET";
    public static final String CHANNELS     = "CHANNELS";
    public static final String END_SITE     = "END_SITE";

    public static final String MOTION     = "MOTION";
    public static final String FRAMES     = "FRAMES";
    public static final String FRAME_TIME = "FRAME_TIME";
    public static final String FRAME      = "FRAME";

    public static final String BONE_TYPE_ROOT  = "ROOT";
    public static final String BONE_TYPE_JOINT = "JOINT";

    public  String       _boneName;
    private String       _boneType;
    private List<String> _channelsProps;
    private float        _frameTime;
    private List<Float>  _frames;
    private String       _lineStr;
    private String       _lineType;
    private int          _nbChannels;
    private int          _nbFrames;
    private float        _offsetX;
    private float        _offsetY;
    private float        _offsetZ;

    public BVHLine(String __lineStr) {
        _parse(__lineStr);
    }

    public String toString() {
        return _lineStr;
    }

    public List<Float> getFrames() {
        return _frames;
    }

    public float getFrameTime() {
        return _frameTime;
    }

    public int getNbFrames() {
        return _nbFrames;
    }

    public List<String> getChannelsProps() {
        return _channelsProps;
    }

    public int getNbChannels() {
        return _nbChannels;
    }

    public float getOffsetZ() {
        return _offsetZ;
    }

    public float getOffsetY() {
        return _offsetY;
    }

    public float getOffsetX() {
        return _offsetX;
    }

    public String getBoneName() {
        return _boneName;
    }

    public String getBoneType() {
        return _boneType;
    }

    public String getLineType() {
        return _lineType;
    }

    private void _parse(String __lineStr) {
        _lineStr = __lineStr;
        _lineStr = _lineStr.trim();
        _lineStr = _lineStr.replace("\t", " ");
        _lineStr = _lineStr.replace("\n", "");
        _lineStr = _lineStr.replace("\r", "");
        _lineStr = _lineStr.replace("Frame Time", "Frame_Time");

        String[] words = _lineStr.split(" ");

        _lineType = _parseLineType(words);

        if (HIERARCHY.equals(_lineType)) {
            // NOOP
        } else if (BONE.equals(_lineType)) {
            _boneType = (words[0].equals("ROOT")) ? BONE_TYPE_ROOT : BONE_TYPE_JOINT;
            _boneName = words[1];
        } else if (OFFSET.equals(_lineType)) {
            _offsetX = Float.parseFloat(words[1]);
            _offsetY = Float.parseFloat(words[2]);
            _offsetZ = Float.parseFloat(words[3]);
        } else if (CHANNELS.equals(_lineType)) {
            _nbChannels    = Integer.parseInt(words[1]);
            _channelsProps = new ArrayList<>();
            for (int i = 0; i < _nbChannels; i++) {
                _channelsProps.add(words[i + 2]);
            }
        } else if (FRAMES.equals(_lineType)) {
            _nbFrames = Integer.parseInt(words[1]);
        } else if (FRAME_TIME.equals(_lineType)) {
            _frameTime = Float.parseFloat(words[1]);
        } else if (FRAME.equals(_lineType)) {
            _frames = new ArrayList<>();
            for (String word : words) {
                _frames.add(Float.valueOf(word));
            }
        } else if (END_SITE.equals(_lineType) ||
                   BRACE_OPEN.equals(_lineType) ||
                   BRACE_CLOSED.equals(_lineType) ||
                   MOTION.equals(_lineType)) {
            // NOOP
        }
    }

    private String _parseLineType(String[] __words) {
        //trace("'" + __words[0] + "' : " + __words[0].length);
        if ("HIERARCHY".equals(__words[0])) {
            return HIERARCHY;
        }
        if ("ROOT".equals(__words[0]) ||
            "JOINT".equals(__words[0])) {
            return BONE;
        }
        if ("{".equals(__words[0])) {
            return BRACE_OPEN;
        }
        if ("}".equals(__words[0])) {
            return BRACE_CLOSED;
        }
        if ("OFFSET".equals(__words[0])) {
            return OFFSET;
        }
        if ("CHANNELS".equals(__words[0])) {
            return CHANNELS;
        }
        if ("End".equals(__words[0])) {
            return END_SITE;
        }
        if ("MOTION".equals(__words[0])) {
            return MOTION;
        }
        if ("Frames:".equals(__words[0])) {
            return FRAMES;
        }
        if ("Frame_Time:".equals(__words[0])) {
            return FRAME_TIME;
        }

        try {
            Float.parseFloat(__words[0]); //check is Parsable
            return FRAME;
        } catch (NumberFormatException e) {
            e.printStackTrace();
        }
        return null;
    }
}