import { useState } from "react";
import { FileDropzone } from "@/components/FileDropzone";
import { FormatSelector } from "@/components/FormatSelector";
import { Download, RefreshCw, Info, Files, Eye } from "lucide-react";
import { cn } from "@/lib/utils";
import { Card, CardContent, CardDescription, CardHeader, CardTitle } from "@/components/ui/card";
import { Button } from "@/components/ui/button";
import { Progress } from "@/components/ui/progress";
import { Tabs, TabsContent, TabsList, TabsTrigger } from "@/components/ui/tabs";
import { Slider } from "@/components/ui/slider";
import { HoverCard, HoverCardContent, HoverCardTrigger } from "@/components/ui/hover-card";
import { useToast } from "@/components/ui/use-toast";
import { Dialog, DialogContent, DialogHeader, DialogTitle, DialogTrigger } from "@/components/ui/dialog";
import { ThemeToggle } from "@/components/ThemeToggle";

interface FileWithProgress {
  file: File;
  progress: number;
  status: 'pending' | 'processing' | 'completed' | 'error';
}

const Index = () => {
  const [selectedFiles, setSelectedFiles] = useState<FileWithProgress[]>([]);
  const [targetFormat, setTargetFormat] = useState("pdf");
  const [isProcessing, setIsProcessing] = useState(false);
  const [progress, setProgress] = useState(0);
  const [compressionLevel, setCompressionLevel] = useState([50]);
  const [previewFile, setPreviewFile] = useState<File | null>(null);
  const { toast } = useToast();

  const handleFileSelect = (file: File) => {
    setSelectedFiles(prev => [...prev, { file, progress: 0, status: 'pending' }]);
  };

  const handleConvert = () => {
    setIsProcessing(true);
    setProgress(0);
    
    // Simulate batch conversion progress
    selectedFiles.forEach((fileData, index) => {
      const interval = setInterval(() => {
        setSelectedFiles(prev => prev.map((f, i) => 
          i === index ? { ...f, progress: Math.min(f.progress + 10, 100), status: f.progress >= 90 ? 'completed' : 'processing' } : f
        ));
      }, 500);

      setTimeout(() => clearInterval(interval), 5000);
    });

    console.log("Converting files:", selectedFiles.map(f => f.file.name));
  };

  const handleCompress = () => {
    if (selectedFiles.length === 0) return;
    
    setIsProcessing(true);
    setProgress(0);

    const interval = setInterval(() => {
      setProgress(prev => {
        if (prev >= 100) {
          clearInterval(interval);
          setIsProcessing(false);
          toast({
            title: "Compression Complete",
            description: `Files compressed to ${compressionLevel}% of original size`,
          });
          return 100;
        }
        return prev + 10;
      });
    }, 500);
  };

  const handlePreview = (file: File) => {
    setPreviewFile(file);
  };

  return (
    <div className="min-h-screen w-full p-4 md:p-8 space-y-8">
      <ThemeToggle />
      
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
            <CardTitle>Convert Your Files</CardTitle>
            <CardDescription>
              Drag and drop multiple files or click to browse
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
                  onFileSelect={handleFileSelect}
                  className="animate-fadeIn"
                />

                {selectedFiles.length > 0 && (
                  <div className="space-y-4">
                    {selectedFiles.map((fileData, index) => (
                      <div key={index} className="flex items-center gap-4 p-4 rounded-lg border">
                        <Files className="h-5 w-5 text-muted-foreground" />
                        <div className="flex-1">
                          <p className="text-sm font-medium">{fileData.file.name}</p>
                          <Progress value={fileData.progress} className="h-2 mt-2" />
                        </div>
                        <Dialog>
                          <DialogTrigger asChild>
                            <Button variant="ghost" size="icon" onClick={() => handlePreview(fileData.file)}>
                              <Eye className="h-4 w-4" />
                            </Button>
                          </DialogTrigger>
                          <DialogContent>
                            <DialogHeader>
                              <DialogTitle>File Preview</DialogTitle>
                            </DialogHeader>
                            <div className="aspect-video bg-muted rounded-lg flex items-center justify-center">
                              <p className="text-muted-foreground">Preview for {fileData.file.name}</p>
                            </div>
                          </DialogContent>
                        </Dialog>
                      </div>
                    ))}
                  </div>
                )}

                <div className="space-y-4 animate-fadeIn">
                  <label className="text-sm font-medium">Convert to:</label>
                  <FormatSelector
                    value={targetFormat}
                    onChange={setTargetFormat}
                    className="w-full"
                  />
                </div>

                <div className="flex gap-4">
                  <Button
                    onClick={handleConvert}
                    disabled={selectedFiles.length === 0 || isProcessing}
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
                        Convert {selectedFiles.length > 1 ? 'All' : 'Now'}
                      </>
                    )}
                  </Button>
                </div>
              </TabsContent>
              <TabsContent value="compress" className="space-y-6">
                <FileDropzone
                  onFileSelect={handleFileSelect}
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
                    disabled={selectedFiles.length === 0 || isProcessing}
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
                  {selectedFiles.length > 0 && isProcessing && (
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