using System;
using System.Collections.Generic;
using System.IO;
using System.Linq;
using System.Text;
using System.Threading.Tasks;
using PDollarGestureRecognizer;
using System.Windows.Forms;

namespace pdollar
{
    class Program
    {
        public static void helpMenu()
        {
            Console.WriteLine(" p$ Help Screen:\n");
            Console.WriteLine(" 1. pdollar –t <gesturefilepath>");
            Console.WriteLine("    Adds the gesture file to the list of gesture templates\n");
            Console.WriteLine(" 2. pdollar ‐r");
            Console.WriteLine("    Clears the templates\n");
            Console.WriteLine(" 3. pdollar <eventstreampath>");
            Console.WriteLine("    Prints the name of gestures recognized from the event stream\n");
        }

        static void Main(string[] args)
        {
            if (args.Length == 0)
            {
                helpMenu();
            }
            else
            {
                switch (args[0])
                {
                    case "-t":
                        String filePath = args[1];

                        if (File.Exists(filePath) == true)
                        {
                            var fileStream = new FileStream(filePath, FileMode.Open, FileAccess.Read);
                            using (var sReader = new StreamReader(fileStream, Encoding.UTF8))
                            {
                                String line;
                                int strokeId = 0;
                                List<Point> pointList = new List<Point>();
                                String gestureName = sReader.ReadLine();

                                while ((line = sReader.ReadLine()) != null)
                                {
                                    if (line == "BEGIN")
                                        strokeId++;
                                    else if (line == "END") { }     //do nothing
                                    else
                                    {
                                        String[] input = line.Split(',');
                                        float x = float.Parse(input[0]);
                                        float y = float.Parse(input[1]);
                                        pointList.Add(new Point(x, y, strokeId));
                                    }
                                }

                                if (!Directory.Exists(Application.StartupPath + "\\GestureSet\\NewGestures"))
                                    Directory.CreateDirectory(Application.StartupPath + "\\GestureSet\\NewGestures");

                                GestureIO.WriteGesture(
                                    pointList.ToArray(),
                                    gestureName,
                                    String.Format("{0}\\GestureSet\\NewGestures\\{1}.xml", Application.StartupPath, gestureName)
                                );
                            }
                            Console.WriteLine("\nGesture Registered!!\n");
                        }
                        else
                        {
                            Console.WriteLine("\nFile not found!!\n");
                            helpMenu();
                        }
                        break;

                    case "-r":
                        filePath = String.Format("{0}\\GestureSet\\NewGestures\\", Application.StartupPath);
                        DirectoryInfo dir = new DirectoryInfo(filePath);
                        foreach(FileInfo file in dir.GetFiles())
                        {
                            if (file.Extension == ".xml")
                                file.Delete();
                        }
                        Console.WriteLine("\nTemplates Removed!!\n");
                        /*
                        if (Directory.Exists(filePath))
                            Directory.Delete(filePath, true);
                        Directory.CreateDirectory(filePath);
                        */

                        break;

                    default:
                        filePath = args[0];
                        if (File.Exists(filePath) == true)
                        {
                            var fileStream = new FileStream(filePath, FileMode.Open, FileAccess.Read);
                            using (var sReader = new StreamReader(fileStream, Encoding.UTF8))
                            {
                                String line;
                                int strokeId = 0;
                                List<Point> pointList = new List<Point>();

                                while ((line = sReader.ReadLine()) != null)
                                {
                                    if (line == "MOUSEDOWN")
                                        strokeId++;
                                    else if (line == "MOUSEUP") { }     //do nothing
                                    else if (line == "RECOGNIZE")
                                    {
                                        if (!Directory.Exists(Application.StartupPath + "\\GestureSet\\NewGestures"))
                                        {
                                            Console.WriteLine("\nGestures not registered!!\n");
                                            Directory.CreateDirectory(Application.StartupPath + "\\GestureSet\\NewGestures");
                                        }

                                        List<Gesture> gestures = new List<Gesture>();
                                        string[] gestureFolders = Directory.GetDirectories(Application.StartupPath + "\\GestureSet");
                                        foreach (string folder in gestureFolders)
                                        {
                                            string[] gestureFiles = Directory.GetFiles(folder, "*.xml");
                                            foreach (string file in gestureFiles)
                                                gestures.Add(GestureIO.ReadGesture(file));
                                        }
                                        Gesture[] trainingSet = gestures.ToArray();

                                        Gesture candidate = new Gesture(pointList.ToArray());
                                        string gestureClass = PointCloudRecognizer.Classify(candidate, trainingSet);

                                        if (gestureClass.Length == 0)
                                            Console.WriteLine("Gesture not recognized");
                                        else
                                            Console.WriteLine("Recognized as: " + gestureClass);
                                    }
                                    else
                                    {
                                        String[] input = line.Split(',');
                                        float x = float.Parse(input[0]);
                                        float y = float.Parse(input[1]);
                                        pointList.Add(new Point(x, y, strokeId));
                                    }
                                }
                            }
                        }
                        else
                        {
                            Console.WriteLine("\nFile not found!!\n");
                            helpMenu();
                        }
                        break;
                }
            }
            //Console.ReadLine();
        }
    }
}