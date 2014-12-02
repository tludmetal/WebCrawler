package com.sewn.crawler;


public class ResultPrinter {
	
	private static String _ALREADYINDEXED = "This link has already been indexed";

	public static int visitedSites;

    public static String Print(LinkedData node)
    {
        return PrintNode(node, false);
    }

    public static String PrintExtended(LinkedData node)
    {
        return PrintNode(node,true);
    }

    public static String PrintResults(LinkedData node)
    {
        return PrintRes(node);
    }

    private static String PrintNode(LinkedData node, Boolean extended)
    {
      
        String text = "";

        text += "<" + node.getAbsoluteLink() + ">" + "\n";

        if (extended)
            text += "\n";


        if (node!=null && node.getChildren()!=null)
        for (LinkedData child : node.getChildren())
        {
            text += "\t<" + child.getAbsoluteLink() + ">";

            if (extended && !child.visited)
                text += "  ---> " + child.reasonNotVisited;

            text += "\n";

            if (extended)
                text += "\n";
        }
        if (node!=null && node.getChildren()!=null)
        for (LinkedData child : node.getChildren())
           if(child.visited)
               text+=PrintNode(child, extended);
            
        
        //System.out.println(text);
        return text;
       
    }


    private static String PrintRes(LinkedData node)
    {

        String text = "";

        text += "<" + node.getAbsoluteLink() + ">" + "\n";
        

        int count = 0;
        if (node!=null && node.getChildren()!=null)
        for (LinkedData child : node.getChildren())
            if (child.visited || child.reasonNotVisited.contentEquals(_ALREADYINDEXED))
                count++;


        text += "\t" + "<No of links to Visited pages: " + count + ">" + "\n";

        if (node!=null && node.getChildren()!=null)
        for (LinkedData child : node.getChildren())
                if (child.visited)
                text += PrintRes(child);

        visitedSites += count;

        return text;

    }

    public static void reset()
    {
        visitedSites = 0;
    }


}
