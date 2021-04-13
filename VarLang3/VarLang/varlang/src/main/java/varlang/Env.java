package varlang;

/**
 * Representation of an environment, which maps variables to values.
 * 
 * @author hridesh
 *
 */
public interface Env {
	Value get (String search_var);
	Boolean getIsEnc ();

	@SuppressWarnings("serial")
	static public class LookupException extends RuntimeException {
		LookupException(String message){
			super(message);
		}
	}
	
	static public class EmptyEnv implements Env {
		public Value get (String search_var) {
			throw new LookupException("No binding found for name: " + search_var);
		}
		public Boolean getIsEnc () { return false; }
	}
	
	static public class ExtendEnv implements Env {
		private Env _saved_env; 
		private String _var; 
		private Value _val;
		private Boolean _isEnc;

		public ExtendEnv(Env saved_env, String var, Value val, Boolean isEnc){
			_saved_env = saved_env;
			_var = var;
			_val = val;
			_isEnc = isEnc;
		}

		public Value get (String search_var) {
			if (search_var.equals(_var))
				return _val;
			return _saved_env.get(search_var);
		}

		public Boolean getIsEnc () {
			return _isEnc;
		}
	}
	
}
