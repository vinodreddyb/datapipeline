package com.cts.datapipeline;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.LineNumberReader;
import java.util.ArrayList;
import java.util.List;

import org.springframework.aop.framework.Advised;
import org.springframework.batch.core.ChunkListener;
import org.springframework.batch.core.ExitStatus;
import org.springframework.batch.core.StepExecution;
import org.springframework.batch.core.StepExecutionListener;
import org.springframework.batch.core.scope.context.ChunkContext;
import org.springframework.batch.item.file.MultiResourceItemReader;
import org.springframework.stereotype.Component;


public class GetCurrentResourceChunkListener implements ChunkListener{

   
    private Object proxy;
    private final List<String> fileNames = new ArrayList<>();

    public void setProxy(Object mrir) {
        this.proxy = mrir;
    }

    @Override
    public void beforeChunk(ChunkContext cc) {
        if (proxy instanceof Advised) {
            try {
                Advised advised = (Advised) proxy;
                Object obj = advised.getTargetSource().getTarget();
                MultiResourceItemReader mrirTarget = (MultiResourceItemReader) obj;
                if (mrirTarget != null
                        && mrirTarget.getCurrentResource() != null
                        && !fileNames.contains(mrirTarget.getCurrentResource().getFilename())) {
                    String fileName = mrirTarget.getCurrentResource().getFilename();
                    fileNames.add(fileName);
                   int count =  getLineCount(mrirTarget.getCurrentResource().getFile());
                   System.out.println("File------->" + fileName);
                    String index = String.valueOf(fileNames.indexOf(fileName));
                    
                    
                   // cc.getStepContext().getStepExecutionContext().put("current.resource" + index, fileName);
                    cc.getStepContext().getStepExecution().getExecutionContext().put("line.count" , count);
                }
            } catch (Exception ex) {
                throw new RuntimeException(ex);
            }
        }
   }
    
    private int getLineCount(File file) throws IOException, FileNotFoundException {
        LineNumberReader lnr = null;
        try {
            lnr = new LineNumberReader(new FileReader(file));
            String line = null;
            int count = 0;
            while ((line = lnr.readLine()) != null) {
                count = lnr.getLineNumber();
            }
            return count;
        } finally {
            if (lnr != null) {
                lnr.close();
            }
        }
    }

    @Override
    public void afterChunk(ChunkContext cc) {
    }

    @Override
    public void afterChunkError(ChunkContext cc) {
    }
}
