package com.shoes.rewardsunlimited;

public class BrandFilter
{
    boolean checked;  //true when checkbox in front of brand name if checked in filter
    String brandName;  //name of brand as coming from server
    String brandNameInTitle; //name of brand in title case

    BrandFilter(boolean checked,String brandName)
    {
        this.checked=checked;
        this.brandName=brandName;
    }

    public boolean isChecked()
    {
        return checked;
    }

    public String convertToTitle(String text)  //function to convert string to string in title case
    {
        if (text==null || text.isEmpty())
        {
            return text;
        }

        StringBuilder converted=new StringBuilder();

        boolean convertNext=true;
        for(char ch:text.toCharArray())
        {
            if(Character.isSpaceChar(ch))
            {
                convertNext=true;
            }
            else if (convertNext)
            {
                ch=Character.toTitleCase(ch);
                convertNext=false;
            }
            else {
                ch=Character.toLowerCase(ch);
            }
            converted.append(ch);
        }
        return converted.toString();
    }
}
