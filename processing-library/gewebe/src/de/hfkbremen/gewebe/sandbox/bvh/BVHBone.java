package de.hfkbremen.gewebe.sandbox.bvh;

import processing.core.PMatrix3D;
import processing.core.PVector;

import java.util.ArrayList;
import java.util.List;

public class BVHBone {

    public PVector absEndPos = new PVector();
    public PVector absPos = new PVector();
    public PMatrix3D global_matrix;
    private String _name;
    private float _offsetX = 0;
    private float _offsetY = 0;
    private float _offsetZ = 0;
    private int _nbChannels;
    private List<String> _channels;
    private float _endOffsetX = 0;
    private float _endOffsetY = 0;
    private float _endOffsetZ = 0;
    private BVHBone _parent;
    private List<BVHBone> _children;
    private float _Xposition = 0;
    private float _Yposition = 0;
    private float _Zposition = 0;
    private float _Xrotation = 0;
    private float _Yrotation = 0;
    private float _Zrotation = 0;

    public BVHBone(BVHBone __parent) {
        _parent = __parent;
        _channels = new ArrayList<String>();
        _children = new ArrayList<BVHBone>();
    }

    public BVHBone() {
        _parent = null;
        _channels = new ArrayList<String>();
        _children = new ArrayList<BVHBone>();
    }

    public String toString() {
        return "[BvhBone] " + _name;
    }

    public String structureToString() {
        return structureToString(0);
    }

    public String structureToString(int __indent) {
        String res = "";
      for (int i = 0; i < __indent; i++) { res += "="; }

        res = res + "> " + _name + "  " + _offsetX + " " + _offsetY + " " + _offsetZ + "\n";
      for (BVHBone child : _children) { res += child.structureToString(__indent + 1); }

        return res;
    }

    public String getName() {
        return _name;
    }

    public void setName(String value) {
        _name = value;
    }

    public Boolean isRoot() {
        return (_parent == null);
    }

    public Boolean hasChildren() {
        return _children.size() > 0;
    }

    public List<BVHBone> getChildren() {
        return _children;
    }

    public void setChildren(List<BVHBone> value) {
        _children = value;
    }

    public BVHBone getParent() {
        return _parent;
    }

    public void setParent(BVHBone value) {
        _parent = value;
    }

    public List<String> getChannels() {
        return _channels;
    }

    public void setChannels(List<String> value) {
        _channels = value;
    }

    public int getNbChannels() {
        return _nbChannels;
    }

    public void setnbChannels(int value) {
        _nbChannels = value;
    }

    //------ position

    public float getZrotation() {
        return _Zrotation;
    }

    public void setZrotation(float value) {
        _Zrotation = value;
    }

    public float getYrotation() {
        return _Yrotation;
    }

    public void setYrotation(float value) {
        _Yrotation = value;
    }

    public float getXrotation() {
        return _Xrotation;
    }

    public void setXrotation(float value) {
        _Xrotation = value;
    }

    public float getZposition() {
        return _Zposition;
    }

    public void setZposition(float value) {
        _Zposition = value;
    }

    public float getYposition() {
        return _Yposition;
    }

    public void setYposition(float value) {
        _Yposition = value;
    }

    public float getXposition() {
        return _Xposition;
    }

    public void setXposition(float value) {
        _Xposition = value;
    }

    public float getEndOffsetZ() {
        return _endOffsetZ;
    }

    public void setEndOffsetZ(float value) {
        _endOffsetZ = value;
    }

    public float getEndOffsetY() {
        return _endOffsetY;
    }

    public void setEndOffsetY(float value) {
        _endOffsetY = value;
    }

    public float getEndOffsetX() {
        return _endOffsetX;
    }

    public void setEndOffsetX(float value) {
        _endOffsetX = value;
    }

    public float getOffsetZ() {
        return _offsetZ;
    }

    public void setOffsetZ(float value) {
        _offsetZ = value;
    }

    public float getOffsetY() {
        return _offsetY;
    }

    public void setOffsetY(float value) {
        _offsetY = value;
    }

    public float getOffsetX() {
        return _offsetX;
    }

    public void setOffsetX(float value) {
        _offsetX = value;
    }

    public PVector getAbsPosition() {
        return absPos;
    }

    public void setAbsPosition(PVector pos) {
        absPos = pos;
    }

    public PVector getAbsEndPosition() {
        return absEndPos;
    }

    public void setAbsEndPosition(PVector pos) {
        absEndPos = pos;
    }
}