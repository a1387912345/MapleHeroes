package net.netty;

import java.nio.ByteOrder;

import io.netty.buffer.AbstractByteBufAllocator;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufAllocator;

public class LittleEndianByteBufAllocator extends AbstractByteBufAllocator {

	private final ByteBufAllocator wrapped;
	
	public LittleEndianByteBufAllocator(ByteBufAllocator wrapped) {
		this.wrapped = wrapped;
	}
	
	@Override
	public boolean isDirectBufferPooled() {
		return false;
	}

	@SuppressWarnings("deprecation")
	@Override
	protected ByteBuf newDirectBuffer(int initialCapacity, int maxCapacity) {
		return wrapped.directBuffer(initialCapacity, maxCapacity).order(ByteOrder.LITTLE_ENDIAN);
	}

	@SuppressWarnings("deprecation")
	@Override
	protected ByteBuf newHeapBuffer(int initialCapacity, int maxCapacity) {
		return wrapped.heapBuffer(initialCapacity, maxCapacity).order(ByteOrder.LITTLE_ENDIAN);
	}

}
