import { useState } from "react";
import { FileDropzone } from "@/components/FileDropzone";
import { FormatSelector } from "@/components/FormatSelector";
import { Download, RefreshCw, Info } from "lucide-react";
import { cn } from "@/lib/utils";
import { Card, CardContent, CardDescription, CardHeader, CardTitle } from "@/components/ui/card";
import { Button } from "@/components/ui/button";
import { Progress } from "@/components/ui/progress";
import { Tabs, TabsContent, TabsList, TabsTrigger } from "@/components/ui/tabs";
import { Slider } from "@/components/ui/slider";
import { HoverCard, HoverCardContent, HoverCardTrigger } from "@/components/ui/hover-card";
import { useToast } from "@/components/ui/use-toast";

const Index = () => {
  const [selectedFile, setSelectedFile] = useState<File | null>(null);
  const [targetFormat, setTargetFormat] = useState("pdf");
  const [isProcessing, setIsProcessing] = useState(false);
  const [progress, setProgress] = useState(0);
  const [compressionLevel, setCompressionLevel] = useState([50]);
  const { toast } = useToast();

  const handleConvert = () => {
    setIsProcessing(true);
    setProgress(0);
    
    // Simulate conversion progress
    const interval = setInterval(() => {
      setProgress((prev) => {
        if (prev >= 100) {
          clearInterval(interval);
          setIsProcessing(false);
          return 100;
        }
        return prev + 10;
      });
    }, 500);

    console.log("Converting", selectedFile?.name, "to", targetFormat);
  };

  const handleCompress = () => {
    if (!selectedFile) {
      toast({
        title: "No file selected",
        description: "Please select a file to compress",
        variant: "destructive",
      });
      return;
    }

    setIsProcessing(true);
    setProgress(0);
    
    // Simulate compression progress
    const interval = setInterval(() => {
      setProgress((prev) => {
        if (prev >= 100) {
          clearInterval(interval);
          setIsProcessing(false);
          toast({
            title: "Compression complete",
            description: `File compressed to ${compressionLevel}% of original size`,
          });
          return 100;
        }
        return prev + 10;
      });
    }, 500);

    console.log("Compressing", selectedFile?.name, "to", compressionLevel, "% of original size");
  };

  return (
    <div className="min-h-screen w-full p-4 md:p-8 space-y-8">
      {/* Hero Section */}
      <header className="text-center space-y-4 animate-fadeIn">
        <h1 className="text-4xl font-bold tracking-tight bg-gradient-to-r from-primary to-primary/60 bg-clip-text text-transparent">
          File Converter
        </h1>
        <p className="text-lg text-muted-foreground max-w-2xl mx-auto">
          Convert your files to any format with ease. Support for PDF, DOCX, images, and more.
        </p>
      </header>

      {/* Main Conversion Card */}
      <main className="max-w-3xl mx-auto space-y-8">
        <Card className="glass animate-fadeIn">
          <CardHeader>
            <CardTitle>Convert Your File</CardTitle>
            <CardDescription>
              Drag and drop your file or click to browse
            </CardDescription>
          </CardHeader>
          <CardContent className="space-y-6">
            <Tabs defaultValue="convert" className="w-full">
              <TabsList className="grid w-full grid-cols-2">
                <TabsTrigger value="convert">Convert</TabsTrigger>
                <TabsTrigger value="compress">Compress</TabsTrigger>
              </TabsList>
              <TabsContent value="convert" className="space-y-6">
                <FileDropzone
                  onFileSelect={setSelectedFile}
                  className="animate-fadeIn"
                />

                <div className="space-y-4 animate-fadeIn">
                  <label className="text-sm font-medium">Convert to:</label>
                  <FormatSelector
                    value={targetFormat}
                    onChange={setTargetFormat}
                    className="w-full"
                  />
                </div>

                {isProcessing && (
                  <div className="space-y-2">
                    <Progress value={progress} className="w-full" />
                    <p className="text-sm text-muted-foreground text-center">
                      Converting... {progress}%
                    </p>
                  </div>
                )}

                <div className="flex gap-4">
                  <Button
                    onClick={handleConvert}
                    disabled={!selectedFile || isProcessing}
                    className="w-full"
                  >
                    {isProcessing ? (
                      <>
                        <RefreshCw className="mr-2 h-4 w-4 animate-spin" />
                        Converting...
                      </>
                    ) : (
                      <>
                        <Download className="mr-2 h-4 w-4" />
                        Convert Now
                      </>
                    )}
                  </Button>
                  {selectedFile && isProcessing && (
                    <Button
                      variant="outline"
                      onClick={() => setIsProcessing(false)}
                      className="w-1/3"
                    >
                      Cancel
                    </Button>
                  )}
                </div>
              </TabsContent>
              <TabsContent value="compress" className="space-y-6">
                <FileDropzone
                  onFileSelect={setSelectedFile}
                  className="animate-fadeIn"
                />

                <div className="space-y-4">
                  <div className="flex items-center justify-between">
                    <label className="text-sm font-medium">Compression Level:</label>
                    <HoverCard>
                      <HoverCardTrigger asChild>
                        <Button variant="ghost" size="icon">
                          <Info className="h-4 w-4" />
                        </Button>
                      </HoverCardTrigger>
                      <HoverCardContent className="w-80">
                        <div className="space-y-2">
                          <h4 className="text-sm font-semibold">About Compression</h4>
                          <p className="text-sm text-muted-foreground">
                            Higher compression levels result in smaller file sizes but may reduce quality.
                            50% is recommended for a good balance between size and quality.
                          </p>
                        </div>
                      </HoverCardContent>
                    </HoverCard>
                  </div>
                  <Slider
                    value={compressionLevel}
                    onValueChange={setCompressionLevel}
                    max={100}
                    step={1}
                    className="w-full"
                  />
                  <p className="text-sm text-muted-foreground text-center">
                    Target file size: {compressionLevel}% of original
                  </p>
                </div>

                {isProcessing && (
                  <div className="space-y-2">
                    <Progress value={progress} className="w-full" />
                    <p className="text-sm text-muted-foreground text-center">
                      Compressing... {progress}%
                    </p>
                  </div>
                )}

                <div className="flex gap-4">
                  <Button
                    onClick={handleCompress}
                    disabled={!selectedFile || isProcessing}
                    className="w-full"
                  >
                    {isProcessing ? (
                      <>
                        <RefreshCw className="mr-2 h-4 w-4 animate-spin" />
                        Compressing...
                      </>
                    ) : (
                      <>
                        <Download className="mr-2 h-4 w-4" />
                        Compress Now
                      </>
                    )}
                  </Button>
                  {selectedFile && isProcessing && (
                    <Button
                      variant="outline"
                      onClick={() => setIsProcessing(false)}
                      className="w-1/3"
                    >
                      Cancel
                    </Button>
                  )}
                </div>
              </TabsContent>
            </Tabs>
          </CardContent>
        </Card>

        {/* Recent Files Section */}
        <section className="space-y-4">
          <h2 className="text-xl font-semibold">Recent Conversions</h2>
          <div className="grid grid-cols-1 md:grid-cols-2 gap-4">
            {/* Recent files will be implemented later */}
          </div>
        </section>
      </main>
    </div>
  );
};

export default Index;