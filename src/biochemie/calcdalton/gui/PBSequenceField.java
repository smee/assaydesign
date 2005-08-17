package biochemie.calcdalton.gui;
import javax.swing.JTextField;
import javax.swing.text.AttributeSet;
import javax.swing.text.BadLocationException;
import javax.swing.text.Document;
import javax.swing.text.PlainDocument;

public class PBSequenceField extends JTextField
{
    protected class CharacterDocument extends PlainDocument
    {

        public void insertString(int offs, String str, AttributeSet a)
            throws BadLocationException
        {
            if(str == null)
                return;

            StringBuffer validstr = new StringBuffer();
            String text = getText(0,getLength());
            if(text == null)
            	text="";
            for(int i = 0; i < str.length(); i++)
            {
                char x;
                if(isUpper)
                    x = Character.toUpperCase(str.charAt(i));
                else
                    x = str.charAt(i);

                if(uniquechars != null && uniquechars.indexOf(x) != -1 && 
                        (text.indexOf(x) != -1 || validstr.indexOf(Character.toString(x))!= -1) )
                	continue;

                if(validChars == null || validChars.indexOf(x) != -1)
                    validstr.append(x);
            }
            super.insertString(offs, new String(validstr), a);

          int len = getLength();
          if(len > maxLen) {
              if(cutfront)
                  remove(0,len - maxLen);
              else
                  remove(maxLen,len-maxLen);
                  
          }
        }

        public CharacterDocument()
        {
        }
    }

    public static final String NUMBERS="0123456789.";
    public static final String CHARACTERS="abcdefghijklmnopqrstuvwxyz" +
    									  "ABCDEFGHIJKLMNOPQRSTUVWXYZ";
    private boolean cutfront=true;

	/**
	 * @return Returns the isUpper.
	 */
	public boolean isUpper() {
		return isUpper;
	}
	/**
	 * @param isUpper The isUpper to set.
	 */
	public void setUpper(boolean isUpper) {
		this.isUpper = isUpper;
	}
	/**
	 * @return Returns the maxLen.
	 */
	public int getMaxLen() {
		return maxLen;
	}
	/**
	 * @param maxLen The maxLen to set.
	 */
	public void setMaxLen(int maxLen) {
		this.maxLen = maxLen;
	}
    /**
     * Cut the front of the string if too long or the end.
     * @param b
     */
    public void cutFront(boolean b) {
        this.cutfront=b;
    }
	/**
	 * @return Returns the validChars.
	 */
	public String getValidChars() {
		return validChars;
	}
	/**
	 * @param validChars The validChars to set.
	 */
	public void setValidChars(String validChars) {
		if(isUpper)
			this.validChars = validChars.toUpperCase();
		else
			this.validChars = validChars;
	}
	public void setUniqueChars(String s){
		this.uniquechars=s;
	}
    boolean isUpper;
    String validChars;
    String uniquechars;
    int maxLen;

    public PBSequenceField(){
    	this(10,false,CHARACTERS);
    }

    public PBSequenceField(int cols, boolean isupper, String validchars)
    {
    	setUpper(isupper);
        setMaxLen(cols);
        setValidChars(validchars);
        setUniqueChars("");
    }

    public void cut()
    {
        super.cut();
    }

    public void paste()
    {
        super.paste();
    }

    public void copy()
    {
        super.copy();
    }

    protected Document createDefaultModel()
    {
        return new CharacterDocument();
    }


}