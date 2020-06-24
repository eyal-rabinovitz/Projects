package httpiotserver;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;

public class ServerMessage implements Message<ProtocolType, Message<?, ?>> , Serializable {
	private static final long serialVersionUID = 1L;
	ProtocolType key;
	Message<?, ?> data;
	
	public ServerMessage(ProtocolType key, Message<?, ?> innerMessageKey) {
		this.key = key;
		this.data = innerMessageKey;
	}

	@Override
	public Message<?, ?> getData() {
		return data;
	}
	
	@Override
	public ProtocolType getKey() {
		return key;
	}
	
	@Override
	public String toString() {
		return "key is = " + key + " data is = " + data.getKey();
	}
	
	/* ***************************    Serializable    *************************** */
	
	public static byte[] toByteArray(Object obj) throws IOException { //TODO delete static
		byte[] bytes = null;
		ByteArrayOutputStream bos = null;
		ObjectOutputStream oos = null;
		try {
			bos = new ByteArrayOutputStream();
			oos = new ObjectOutputStream(bos);
			oos.writeObject(obj);
			oos.flush();
			bytes = bos.toByteArray();
		} finally {
			if (oos != null) {
				oos.close();
			}
			if (bos != null) {
				bos.close();
			}
		}
		return bytes;
	}
	
	 public static Object toObject(byte[] bytes) throws IOException, ClassNotFoundException {//TODO delete static
	        Object obj = null;
	        ByteArrayInputStream bis = null;
	        ObjectInputStream ois = null;
	        try {
	            bis = new ByteArrayInputStream(bytes);
	            ois = new ObjectInputStream(bis);
	            obj = ois.readObject();
	        } finally {
	            if (bis != null) {
	                bis.close();
	            }
	            if (ois != null) {
	                ois.close();
	            }
	        }
	        return obj;
	  }

	    public String toString(byte[] bytes) {
	        return new String(bytes);
	    }
}





/*
package il.co.ilrd.pingpong.handlers;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;

public class ServerMessage<K, D> implements Message<Integer, Message<K, D>> , Serializable {
	private static final long serialVersionUID = 1L;
	Integer key;
	Message<K, D> data;
	
	public ServerMessage(Integer key, Message<K, D> innerMessageKey) {
		this.key = key;
		this.data = new InnerMessage(innerMessageKey);
	}
	
	public ServerMessage(Integer key, String innerMessageKey) {
		this.key = key;
		this.data = new InnerMessage(innerMessageKey);
	}

	@Override
	public Message<K, D> getData() {
		return data;
	}
	
	@Override
	public Integer getKey() {
		return key;
	}
	
	@Override
	public String toString() {
		return "key is = " + key + " data is = " + data.getKey();
	}
	
	private class InnerMessage implements Message<K, D> , Serializable{
		private static final long serialVersionUID = 2L;
		K key;
		D data;
		
		private InnerMessage(Message<K, D> message) {
			key = message.getKey();
			data = message.getData();
		}
		
		private InnerMessage(String message) {
			key = (K)message;
			data = null;
		}
		
		@Override
		public K getKey() {
			return key;
		}

		@Override
		public D getData() {
			return data;
		}
		
		@Override
		public String toString() {
			return "key is = " + key + " data is = " + data;
		}
	}
	
	
	public byte[] toByteArray(Object obj) throws IOException {
		byte[] bytes = null;
		ByteArrayOutputStream bos = null;
		ObjectOutputStream oos = null;
		try {
			bos = new ByteArrayOutputStream();
			oos = new ObjectOutputStream(bos);
			oos.writeObject(obj);
			oos.flush();
			bytes = bos.toByteArray();
		} finally {
			if (oos != null) {
				oos.close();
			}
			if (bos != null) {
				bos.close();
			}
		}
		return bytes;
	}
	
	 public Object toObject(byte[] bytes) throws IOException, ClassNotFoundException {
	        Object obj = null;
	        ByteArrayInputStream bis = null;
	        ObjectInputStream ois = null;
	        try {
	            bis = new ByteArrayInputStream(bytes);
	            ois = new ObjectInputStream(bis);
	            obj = ois.readObject();
	        } finally {
	            if (bis != null) {
	                bis.close();
	            }
	            if (ois != null) {
	                ois.close();
	            }
	        }
	        return obj;
	  }

	    public String toString(byte[] bytes) {
	        return new String(bytes);
	    }
}*/
