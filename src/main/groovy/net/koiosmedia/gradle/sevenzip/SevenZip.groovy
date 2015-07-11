package net.koiosmedia.gradle.sevenzip;

import java.io.File;

import org.gradle.api.*
import org.gradle.api.tasks.bundling.AbstractArchiveTask
import org.gradle.api.tasks.*
import org.gradle.api.file.FileCollection
import org.gradle.api.file.FileCopyDetails
import org.gradle.api.internal.DocumentationRegistry
import org.gradle.api.internal.file.CopyActionProcessingStreamAction
import org.gradle.api.internal.file.copy.CopyAction
import org.gradle.api.internal.file.copy.CopyActionProcessingStream
import org.gradle.api.internal.file.copy.FileCopyDetailsInternal
import org.gradle.api.internal.tasks.SimpleWorkResult

class SevenZip extends AbstractArchiveTask {
	private static class SevenZipCopyAction implements CopyAction {
		private static class CollectFilesStreamAction implements CopyActionProcessingStreamAction {
			private final List<File> files;
	
			public CollectFilesStreamAction() {
				files=new LinkedList<>();
			}
	
			@Override
			public void processFile(FileCopyDetailsInternal details) {
				files.add(details.getFile());
			}
			
			public List<File> getFiles() {
				return files;
			}
		}
		
		private final File archiveFile;
		
		public SevenZipCopyAction(final File archiveFile) {
			this.archiveFile=archiveFile;
		}
		
		@Override
		public WorkResult execute(final CopyActionProcessingStream stream) {
			CollectFilesStreamAction collectFiles=new CollectFilesStreamAction();
			
			stream.process(collectFiles);
			
			com.swemel.sevenzip.SevenZip sevenZip =
				new com.swemel.sevenzip.SevenZip(archiveFile.getAbsolutePath(), toFileArray(collectFiles.getFiles()));
	
			sevenZip.createArchive();
			
			return new SimpleWorkResult(true);
		}
		
		private File[] toFileArray(List<File> files) {
			File[] fileArray=new File[files.size()];
			
			for(int i=0; i<files.size(); i++) {
				fileArray[i]=files.get(i);
			}
			
			return fileArray;
		}
	}
	
	public static final String SEVENZ_EXTENSION = "7z";
	
	public SevenZip() {
		setExtension(SEVENZ_EXTENSION);
	}
	
	@Override
	protected CopyAction createCopyAction() {
		return new SevenZipCopyAction(getArchivePath());
	}
}
