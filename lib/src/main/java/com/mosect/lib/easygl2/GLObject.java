package com.mosect.lib.easygl2;

/**
 * opengl Object
 */
public abstract class GLObject implements AutoCloseable {

    private int state = 0;
    private GLContext context = null;

    /**
     * 初始化对象
     *
     * @param context 当前opengl环境对象
     */
    public void init(GLContext context) {
        if (state != 0) {
            throw new IllegalStateException("Initialed or destroyed");
        }
        state = 1;
        this.context = context;
        try {
            onInit();
        } catch (Exception e) {
            onClear();
            throw e;
        }
        state = 2;
    }

    /**
     * 释放对象
     *
     * @throws GLException opengl异常
     */
    @Override
    public void close() throws GLException {
        if (state != 3) {
            onClose();
            context = null;
            state = 3;
        }
    }

    /**
     * 清楚对象资源，在此方法进行资源释放
     *
     * @throws GLException opengl异常
     */
    protected abstract void onClear() throws GLException;

    /**
     * 初始化对象资源
     *
     * @throws GLException opengl异常
     */
    protected abstract void onInit() throws GLException;

    /**
     * 对象被释放，这里直接调用{@link #onClear()}
     *
     * @throws GLException opengl异常
     */
    protected void onClose() throws GLException {
        onClear();
    }

    /**
     * 获取当前opengl上下文对象
     *
     * @return 当前opengl上下文对象
     */
    public GLContext getContext() {
        return context;
    }
}
