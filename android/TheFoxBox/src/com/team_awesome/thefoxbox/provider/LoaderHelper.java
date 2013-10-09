package com.team_awesome.thefoxbox.provider;

import java.io.IOException;

import com.team_awesome.thefoxbox.data.Mood;
import com.team_awesome.thefoxbox.data.SongItem;

import android.os.AsyncTask;
import android.util.Log;

/**
 * This class represents the glue between the UI and talking to the server. Because it faces the UI and calls
 * the data layer, it may seem like there is a bit of duplication. This duplication is not strictly needed, but
 * it serves as a single point at which we deal with threading so that the UI/data retrieval doesn't have to worry
 * about it as much.
 *  
 * @author Kevin
 *
 * TODO: Use an Executor so that we can have multiple requests out at a time.
 * @param <Ret>
 */
public class LoaderHelper<Ret> extends AsyncTask<Object, Void, Ret> {
	public interface Callback<E> {
		void done(E ret);
		void err(Exception ex);
	}
	
	private static final CommunicatorFactory fact = new CommunicatorFactory("192.168.1.108", 5853);
	
	public static void init(LoginInfo logfo) {
		fact.init(logfo);
	}
	
	private final CallImpls type;
	private final Callback<Ret> call;
	private LoaderHelper(CallImpls t, Callback<Ret> back) {
		type = t;
		call = back;
	}
	
	@Override
	@SuppressWarnings("unchecked")
	protected Ret doInBackground(Object... params) {
		try {
			return (Ret)type.s(params);
		} catch (IOException ex) {
			return (Ret)ex;
		}
	}
	
	@Override
	protected void onPostExecute(Ret result) {
		if (result instanceof Exception) {
			// We want the gui thread to know about this.
			call.err((Exception)result);
		} else {
			if (call == null) {
				Log.w("LoaderHelper", "Call to " + type + " didn't provide a callback. Return: " + result);
			} else {
				call.done(result);
			}
		}
	}
	
	
	public static void getSongList(Callback<SongItem[]> call) {
		new LoaderHelper<SongItem[]>(CallImpls.SONGLIST, call).execute();
	}
	
	public static void search(Callback<SongItem[]> call, String term) {
		new LoaderHelper<SongItem[]>(CallImpls.SEARCH, call).execute(term);
	}
	
	// Currently doesn't return a value
	public static void vote(int songid, int voteamt) {
		new LoaderHelper<Void>(CallImpls.VOTE, null).execute(songid, voteamt);
	}
	
	// Currently doesn't return a value
	public static void submit(Callback<String> call, int songid) {
		new LoaderHelper<String>(CallImpls.SUBMIT, call).execute(songid);
	}
	
	public static void ping(Callback<Boolean> call) {
		new LoaderHelper<Boolean>(CallImpls.PING, call).execute();
	}
	
	public static void moodchange(Mood m) {
		new LoaderHelper<Void>(CallImpls.MOODCHANGE, null).execute(m);
	}
	
	public static void suggest(Callback<SongItem[]> call) {
		new LoaderHelper<SongItem[]>(CallImpls.SUGGEST, call).execute();
	}
	
	private static enum CallImpls {
		SONGLIST() {
			@Override
			Object s(Object[] _) throws IOException {
				return fact.get().getSongList();
			}
		}, SEARCH() {
			@Override
			Object s(Object[] params) throws IOException {
				return fact.get().search((String)params[0]);
			}
		}, VOTE() {
			@Override
			Object s(Object[] params) throws IOException {
				fact.get().vote((Integer)params[0], (Integer)params[1]);
				return null;
			}
		}, SUBMIT() {
			@Override
			Object s(Object[] params) throws IOException {
				fact.get().submit((Integer)params[0]);
				return null;
			}
		}, PING() {
			@Override
			Object s(Object[] _) throws IOException {
				return fact.get().ping();
			}
		}, MOODCHANGE() {
			@Override
			Object s(Object[] params) throws IOException {
				fact.get().moodchange((Mood)params[0]);
				return null;
			}
		}, SUGGEST() {
			@Override
			Object s(Object[] _) throws IOException {
				return fact.get().suggest();
			}
		};
		
		abstract Object s(Object[] params) throws IOException;
	}
}










