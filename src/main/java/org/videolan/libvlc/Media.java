/*****************************************************************************
 * Media.java
 *****************************************************************************
 * Copyright © 2011-2013 VLC authors and VideoLAN
 *
 * This program is free software; you can redistribute it and/or modify it
 * under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation; either version 2.1 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with this program; if not, write to the Free Software Foundation,
 * Inc., 51 Franklin Street, Fifth Floor, Boston MA 02110-1301, USA.
 *****************************************************************************/

package org.videolan.libvlc;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.HashSet;
import java.util.Locale;

import android.graphics.Bitmap;
import android.text.TextUtils;
import android.util.Log;

public class Media implements Comparable<Media> {
    public final static String TAG = "VLC/LibVLC/Media";

    public final static HashSet<String> VIDEO_EXTENSIONS;
    public final static HashSet<String> AUDIO_EXTENSIONS;
    public final static String EXTENSIONS_REGEX;
    public final static HashSet<String> FOLDER_BLACKLIST;

    static {
        String[] video_extensions = {
                ".3g2", ".3gp", ".3gp2", ".3gpp", ".amv", ".asf", ".avi", ".divx", ".drc", ".dv",
                ".f4v", ".flv", ".gvi", ".gxf", ".ismv", ".iso", ".m1v", ".m2v", ".m2t", ".m2ts",
                ".m4v", ".mkv", ".mov", ".mp2", ".mp2v", ".mp4", ".mp4v", ".mpe", ".mpeg",
                ".mpeg1", ".mpeg2", ".mpeg4", ".mpg", ".mpv2", ".mts", ".mtv", ".mxf", ".mxg",
                ".nsv", ".nut", ".nuv", ".ogm", ".ogv", ".ogx", ".ps", ".rec", ".rm", ".rmvb",
                ".tod", ".ts", ".tts", ".vob", ".vro", ".webm", ".wm", ".wmv", ".wtv", ".xesc" };

        String[] audio_extensions = {
                ".3ga", ".a52", ".aac", ".ac3", ".adt", ".adts", ".aif", ".aifc", ".aiff", ".amr",
                ".aob", ".ape", ".awb", ".caf", ".dts", ".flac", ".it", ".m4a", ".m4b", ".m4p",
                ".mid", ".mka", ".mlp", ".mod", ".mpa", ".mp1", ".mp2", ".mp3", ".mpc", ".mpga",
                ".oga", ".ogg", ".oma", ".opus", ".ra", ".ram", ".rmi", ".s3m", ".spx", ".tta",
                ".voc", ".vqf", ".w64", ".wav", ".wma", ".wv", ".xa", ".xm" };

        String[] folder_blacklist = {
                "/alarms",
                "/notifications",
                "/ringtones",
                "/media/alarms",
                "/media/notifications",
                "/media/ringtones",
                "/media/audio/alarms",
                "/media/audio/notifications",
                "/media/audio/ringtones",
                "/Android/data/" };

        VIDEO_EXTENSIONS = new HashSet<String>();
        for (String item : video_extensions)
            VIDEO_EXTENSIONS.add(item);
        AUDIO_EXTENSIONS = new HashSet<String>();
        for (String item : audio_extensions)
            AUDIO_EXTENSIONS.add(item);

        StringBuilder sb = new StringBuilder(115);
        sb.append(".+(\\.)((?i)(");
        sb.append(video_extensions[0].substring(1));
        for(int i = 1; i < video_extensions.length; i++) {
            sb.append('|');
            sb.append(video_extensions[i].substring(1));
        }
        for(int i = 0; i < audio_extensions.length; i++) {
            sb.append('|');
            sb.append(audio_extensions[i].substring(1));
        }
        sb.append("))");
        EXTENSIONS_REGEX = sb.toString();
        FOLDER_BLACKLIST = new HashSet<String>();
        for (String item : folder_blacklist)
            FOLDER_BLACKLIST.add(android.os.Environment.getExternalStorageDirectory().getPath() + item);
    }

    public final static int TYPE_ALL = -1;
    public final static int TYPE_VIDEO = 0;
    public final static int TYPE_AUDIO = 1;
    public final static int TYPE_GROUP = 2;

    /** Metadata from libvlc_media */
    protected String mTitle;
    private String mArtist;
    private String mGenre;
    private String mCopyright;
    private String mAlbum;
    private int mTrackNumber;
    private String mAlbumArtist;
    private String mDescription;
    private String mRating;
    private String mDate;
    private String mSettings;
    private String mNowPlaying;
    private String mPublisher;
    private String mEncodedBy;
    private String mTrackID;
    private String mArtworkURL;

    public final static int libvlc_meta_Title       = 0;
    public final static int libvlc_meta_Artist      = 1;
    public final static int libvlc_meta_Genre       = 2;
//    public final static int libvlc_meta_Copyright   = 3;
    public final static int libvlc_meta_Album       = 4;
//    public final static int libvlc_meta_TrackNumber = 5;
//    public final static int libvlc_meta_Description = 6;
//    public final static int libvlc_meta_Rating      = 7;
//    public final static int libvlc_meta_Date        = 8;
//    public final static int libvlc_meta_Setting     = 9;
//    public final static int libvlc_meta_URL         = 10;
//    public final static int libvlc_meta_Language    = 11;
    public final static int libvlc_meta_NowPlaying  = 12;
//    public final static int libvlc_meta_Publisher   = 13;
//    public final static int libvlc_meta_EncodedBy   = 14;
    public final static int libvlc_meta_ArtworkURL  = 15;
//    public final static int libvlc_meta_TrackID     = 16;
//    public final static int libvlc_meta_TrackTotal  = 17;
//    public final static int libvlc_meta_Director    = 18;
//    public final static int libvlc_meta_Season      = 19;
//    public final static int libvlc_meta_Episode     = 20;
//    public final static int libvlc_meta_ShowName    = 21;
//    public final static int libvlc_meta_Actors      = 22;

    private final String mLocation;
    private String mFilename;
    private long mTime = 0;
    private int mAudioTrack = -1;
    private int mSpuTrack = -2;
    private long mLength = 0;
    private int mType;
    private int mWidth = 0;
    private int mHeight = 0;
    private Bitmap mPicture;
    private boolean mIsPictureParsed;

    /**
     * Create a new Media
     * @param libVLC A pointer to the libVLC instance. Should not be NULL
     * @param URI The URI of the media.
     */
    public Media(LibVLC libVLC, String URI) {
        if(libVLC == null)
            throw new NullPointerException("libVLC was null");

        mLocation = URI;

        mType = TYPE_ALL;
        TrackInfo[] tracks = libVLC.readTracksInfo(mLocation);

        extractTrackInfo(tracks);
    }

    private void extractTrackInfo(TrackInfo[] tracks) {
        if (tracks == null) {
            mTitle = null;
            mArtist = getValueWrapper(null, UnknownStringType.Artist).trim();
            mAlbum = getValueWrapper(null, UnknownStringType.Album).trim();
            mGenre = getValueWrapper(null, UnknownStringType.Genre).trim();
            mAlbumArtist = getValueWrapper(null, UnknownStringType.AlbumArtist).trim();
            return;
        }

        for (TrackInfo track : tracks) {
            if (track.Type == TrackInfo.TYPE_VIDEO) {
                mType = TYPE_VIDEO;
                mWidth = track.Width;
                mHeight = track.Height;
            } else if (mType == TYPE_ALL && track.Type == TrackInfo.TYPE_AUDIO){
                mType = TYPE_AUDIO;
            } else if (track.Type == TrackInfo.TYPE_META) {
                mLength = track.Length;
                mTitle = track.Title != null ? track.Title.trim() : null;
                mArtist = getValueWrapper(track.Artist, UnknownStringType.Artist).trim();
                mAlbum = getValueWrapper(track.Album, UnknownStringType.Album).trim();
                mGenre = getValueWrapper(track.Genre, UnknownStringType.Genre).trim();
                mAlbumArtist = getValueWrapper(track.AlbumArtist, UnknownStringType.AlbumArtist).trim();
                mArtworkURL = track.ArtworkURL;
                mNowPlaying = track.NowPlaying;
                if (!TextUtils.isEmpty(track.TrackNumber)) {
                    try {
                        mTrackNumber = Integer.parseInt(track.TrackNumber);
                    } catch (NumberFormatException ignored) {
                    }
                }
                Log.d(TAG, "Title " + mTitle);
                Log.d(TAG, "Artist " + mArtist);
                Log.d(TAG, "Genre " + mGenre);
                Log.d(TAG, "Album " + mAlbum);
            }
        }

        /* No useful ES found */
        if (mType == TYPE_ALL) {
            int dotIndex = mLocation.lastIndexOf(".");
            if (dotIndex != -1) {
                String fileExt = mLocation.substring(dotIndex).toLowerCase(Locale.ENGLISH);
                if( Media.VIDEO_EXTENSIONS.contains(fileExt) ) {
                    mType = TYPE_VIDEO;
                } else if (Media.AUDIO_EXTENSIONS.contains(fileExt)) {
                    mType = TYPE_AUDIO;
                }
            }
        }
    }

    public Media(String location, long time, long length, int type,
            Bitmap picture, String title, String artist, String genre, String album, String albumArtist,
            int width, int height, String artworkURL, int audio, int spu, int trackNumber) {
        mLocation = location;
        mFilename = null;
        mTime = time;
        mAudioTrack = audio;
        mSpuTrack = spu;
        mLength = length;
        mType = type;
        mPicture = picture;
        mWidth = width;
        mHeight = height;

        mTitle = title;
        mArtist = getValueWrapper(artist, UnknownStringType.Artist);
        mGenre = getValueWrapper(genre, UnknownStringType.Genre);
        mAlbum = getValueWrapper(album, UnknownStringType.Album);
        mAlbumArtist = getValueWrapper(albumArtist, UnknownStringType.AlbumArtist);
        mArtworkURL = artworkURL;
        mTrackNumber = trackNumber;
    }

    private enum UnknownStringType { Artist , Genre, Album, AlbumArtist };
    /**
     * Uses introspection to read VLC l10n databases, so that we can sever the
     * hard-coded dependency gracefully for 3rd party libvlc apps while still
     * maintaining good l10n in VLC for Android.
     *
     * @see org.videolan.vlc.util.Util#getValue(String, int)
     *
     * @param string The default string
     * @param type Alias for R.string.xxx
     * @return The default string if not empty or string from introspection
     */
    private static String getValueWrapper(String string, UnknownStringType type) {
        if(string != null && string.length() > 0) return string;

        try {
            Class<?> stringClass = Class.forName("org.videolan.vlc.R$string");
            Class<?> utilClass = Class.forName("org.videolan.vlc.Util");

            Integer value;
            switch(type) {
            case Album:
                value = (Integer)stringClass.getField("unknown_album").get(null);
                break;
            case Genre:
                value = (Integer)stringClass.getField("unknown_genre").get(null);
                break;
            case AlbumArtist:
                value = (Integer)stringClass.getField("unknown_artist").get(null);
                break;
            case Artist:
            default:
                value = (Integer)stringClass.getField("unknown_artist").get(null);
                break;
            }

            Method getValueMethod = utilClass.getDeclaredMethod("getValue", String.class, Integer.TYPE);
            // Util.getValue(string, R.string.xxx);
            return (String) getValueMethod.invoke(null, string, value);
        } catch (ClassNotFoundException e) {
        } catch (IllegalArgumentException e) {
        } catch (IllegalAccessException e) {
        } catch (NoSuchFieldException e) {
        } catch (NoSuchMethodException e) {
        } catch (InvocationTargetException e) {
        }

        // VLC for Android translations not available (custom app perhaps)
        // Use hardcoded English phrases.
        switch(type) {
        case Album:
            return "Unknown Album";
        case Genre:
            return "Unknown Genre";
        case Artist:
        default:
            return "Unknown Artist";
        }
    }

    /**
     * Compare the filenames to sort items
     */
    @Override
    public int compareTo(Media another) {
        return getTitle().toUpperCase(Locale.getDefault()).compareTo(
                another.getTitle().toUpperCase(Locale.getDefault()));
    }

    public String getLocation() {
        return mLocation;
    }

    public void updateMeta(LibVLC libVLC) {
        mTitle = libVLC.getMeta(libvlc_meta_Title);
        mArtist = getValueWrapper(libVLC.getMeta(libvlc_meta_Artist), UnknownStringType.Artist);
        mGenre = getValueWrapper(libVLC.getMeta(libvlc_meta_Genre), UnknownStringType.Genre);
        mAlbum = getValueWrapper(libVLC.getMeta(libvlc_meta_Album), UnknownStringType.Album);
        mNowPlaying = libVLC.getMeta(libvlc_meta_NowPlaying);
        mArtworkURL = libVLC.getMeta(libvlc_meta_ArtworkURL);
    }

    public String getFileName() {
        if (mFilename == null) {
            mFilename = LibVlcUtil.URItoFileName(mLocation);
        }
        return mFilename;
    }

    public long getTime() {
        return mTime;
    }

    public void setTime(long time) {
        mTime = time;
    }

    public int getAudioTrack() {
        return mAudioTrack;
    }

    public void setAudioTrack(int track) {
        mAudioTrack = track;
    }

    public int getSpuTrack() {
        return mSpuTrack;
    }

    public void setSpuTrack(int track) {
        mSpuTrack = track;
    }

    public long getLength() {
        return mLength;
    }

    public int getType() {
        return mType;
    }

    public int getWidth() {
        return mWidth;
    }

    public int getHeight() {
        return mHeight;
    }

    /**
     * Returns the raw picture object. Likely to be NULL in VLC for Android
     * due to lazy-loading.
     *
     * Use {@link org.videolan.vlc.util.Bitmap#getPictureFromCache(Media)} instead.
     *
     * @return The raw picture or NULL
     */
    public Bitmap getPicture() {
        return mPicture;
    }

    /**
     * Sets the raw picture object.
     *
     * In VLC for Android, use {@link org.videolan.vlc.MediaDatabase#setPicture(Media, Bitmap)} instead.
     *
     * @param p
     */
    public void setPicture(Bitmap p) {
        mPicture = p;
    }

    public boolean isPictureParsed() {
        return mIsPictureParsed;
    }

    public void setPictureParsed(boolean isParsed) {
        mIsPictureParsed = isParsed;
    }

    public String getTitle() {
        if (mTitle != null && mType != TYPE_VIDEO)
            return mTitle;
        else {
            String fileName = getFileName();
            if (fileName == null)
                return "";
            int end = fileName.lastIndexOf(".");
            if (end <= 0)
                return fileName;
            return fileName.substring(0, end);
        }
    }

    public String getSubtitle() {
        return mType != TYPE_VIDEO ?
                mNowPlaying != null ?
                        mNowPlaying
                        : mArtist + " - " + mAlbum
                : "";
    }

    public String getReferenceArtist() {
        if (isAlbumArtistUnknown())
            return mArtist;
        else
            return mAlbumArtist;
    }

    public String getArtist() {
        return mArtist;
    }

    public Boolean isAlbumArtistUnknown() {
        return (mAlbumArtist.equals(getValueWrapper(null, UnknownStringType.AlbumArtist)));
    }

    public Boolean isArtistUnknown() {
        return (mArtist.equals(getValueWrapper(null, UnknownStringType.Artist)));
    }

    public String getGenre() {
        if(getValueWrapper(null, UnknownStringType.Genre).equals(mGenre))
            return mGenre;
        else if( mGenre.length() > 1)/* Make genres case insensitive via normalisation */
            return Character.toUpperCase(mGenre.charAt(0)) + mGenre.substring(1).toLowerCase(Locale.getDefault());
        else
            return mGenre;
    }

    public String getCopyright() {
        return mCopyright;
    }

    public String getAlbum() {
        return mAlbum;
    }

    public String getAlbumArtist() {
        return mAlbumArtist;
    }

    public Boolean isAlbumUnknown() {
        return (mAlbum.equals(getValueWrapper(null, UnknownStringType.Album)));
    }

    public int getTrackNumber() {
        return mTrackNumber;
    }

    public String getDescription() {
        return mDescription;
    }

    public String getRating() {
        return mRating;
    }

    public String getDate() {
        return mDate;
    }

    public String getSettings() {
        return mSettings;
    }

    public String getNowPlaying() {
        return mNowPlaying;
    }

    public String getPublisher() {
        return mPublisher;
    }

    public String getEncodedBy() {
        return mEncodedBy;
    }

    public String getTrackID() {
        return mTrackID;
    }

    public String getArtworkURL() {
        return mArtworkURL;
    }
}
