package com.github.obase.app;

/**
 * 确定APP回调流程: init --> exec --> done(如果还有异常则是中断)
 *
 */
public abstract class App {

	// 声明参数, 包括添加添加
	public void declare(Flags args) {
		// nothing
	}

	// 处理进程,最后返回exit_code
	public abstract int execute(Context ctx, Flags args) throws Exception;

	// 销毁进程
	public void destroy(Context ctx, Exception e) {
		// nothing

	}

}
