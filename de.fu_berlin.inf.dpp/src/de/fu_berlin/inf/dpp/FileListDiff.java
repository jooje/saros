package de.fu_berlin.inf.dpp;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.SubMonitor;

import de.fu_berlin.inf.dpp.FileList.FileListData;

/**
 * A diff between two {@link FileList}s.
 * 
 * TODO Make this a public static internal class of FileList?
 * 
 * @see FileList#diff(FileList)
 * 
 * @author ahaferburg
 */
public class FileListDiff {
    private FileListDiff() {
        // Empty but non-public. Only FileList should create FileListDiffs.
    }

    private final List<IPath> added = new ArrayList<IPath>();

    private final List<IPath> removed = new ArrayList<IPath>();

    private final List<IPath> altered = new ArrayList<IPath>();

    private final List<IPath> unaltered = new ArrayList<IPath>();

    /**
     * Returns a new FileListDiff which contains the difference of the two
     * FileLists.
     * 
     * @param base
     *            The base <code>FileList</code>.
     * @param target
     *            The <code>FileList</code> to compare to.
     * 
     * @return a new <code>FileListDiff</code> which contains the difference
     *         information of the two <code>FileList</code>s.
     * 
     *         The diff describes the operations needed to get from
     *         <code>base</code> to <code>target</code>.
     * @see FileListDiff
     */
    public static FileListDiff diff(FileList base, FileList target) {
        FileListDiff result = new FileListDiff();
        if (base == null || target == null)
            return result;

        for (Map.Entry<IPath, FileListData> entry : base.entries.entrySet()) {
            if (!target.entries.containsKey(entry.getKey())) {
                result.removed.add(entry.getKey());
            }
        }

        for (Map.Entry<IPath, FileListData> entry : target.entries.entrySet()) {
            if (!base.entries.containsKey(entry.getKey())) {
                result.added.add(entry.getKey());
            }
        }

        for (Map.Entry<IPath, FileListData> entry : base.entries.entrySet()) {
            IPath path = entry.getKey();
            if (target.entries.containsKey(path)) {

                if (path.hasTrailingSeparator()) {
                    result.unaltered.add(path);
                } else {
                    long checksum = entry.getValue().checksum;
                    long otherChecksum = target.entries.get(path).checksum;

                    if (checksum == otherChecksum) {
                        result.unaltered.add(path);
                    } else {
                        result.altered.add(path);
                    }
                }
            }
        }

        return result;
    }

    /**
     * @return The sorted list of paths that were removed.
     */
    public List<IPath> getRemovedPaths() {
        return sorted(this.removed);
    }

    public List<IPath> getUnalteredPaths() {
        return sorted(this.unaltered);
    }

    public List<IPath> getAddedPaths() {
        return sorted(this.added);
    }

    public List<IPath> getAlteredPaths() {
        return sorted(this.altered);
    }

    private List<IPath> sorted(List<IPath> paths) {
        Collections.sort(paths, new FileList.PathLengthComparator());
        return new ArrayList<IPath>(paths);
    }

    /**
     * Will create all folders contained in this FileList for the given project
     * and return a FileList which does not contain these folders.
     * 
     * Note: All parent folders of any folder contained in the FileList must be
     * contained as well.
     * 
     * @throws CoreException
     */
    public FileListDiff addAllFolders(IProject localProject, SubMonitor monitor)
        throws CoreException {

        List<IPath> toCheck = this.getAddedPaths();
        monitor.beginTask("Adding folders", toCheck.size());

        FileListDiff result = new FileListDiff();
        result.altered.addAll(this.altered);
        result.removed.addAll(this.removed);
        result.unaltered.addAll(this.unaltered);

        for (IPath path : toCheck) {

            if (path.hasTrailingSeparator()) {
                IFolder folder = localProject.getFolder(path);
                if (!folder.exists()) {
                    monitor.subTask("Creating folder " + path.lastSegment());
                    folder.create(true, true, monitor.newChild(1));
                    continue;
                }
            } else {
                result.added.add(path);
            }
            monitor.worked(1);
        }
        // result.readResolve();

        monitor.done();
        return result;
    }

    /**
     * Removes all resources marked as removed in this FileList from the given
     * project.
     * 
     * @param localProject
     *            the local project were the shared project will be replicated.
     * @throws CoreException
     */
    public FileListDiff removeUnneededResources(IProject localProject,
        SubMonitor monitor) throws CoreException {

        // TODO don't throw CoreException
        // TODO check if this triggers the resource listener

        List<IPath> toDelete = this.getRemovedPaths();
        monitor.beginTask("Removing resources", toDelete.size());

        for (IPath path : toDelete) {

            monitor.subTask("Deleting " + path.lastSegment());
            if (path.hasTrailingSeparator()) {
                IFolder folder = localProject.getFolder(path);

                if (folder.exists()) {
                    folder.delete(true, monitor.newChild(1));
                }

            } else {
                IFile file = localProject.getFile(path);

                // check if file exists because it might have already been
                // deleted when deleting its folder
                if (file.exists()) {
                    file.delete(true, monitor.newChild(1));
                }
            }
        }

        FileListDiff result = new FileListDiff();
        result.added.addAll(this.added);
        result.altered.addAll(this.altered);
        // Removed is empty now
        result.unaltered.addAll(this.unaltered);

        monitor.done();

        return result;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((added == null) ? 0 : added.hashCode());
        result = prime * result + ((altered == null) ? 0 : altered.hashCode());
        result = prime * result + ((removed == null) ? 0 : removed.hashCode());
        result = prime * result
            + ((unaltered == null) ? 0 : unaltered.hashCode());
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }

        if (!(obj instanceof FileListDiff)) {
            return false;
        }

        FileListDiff other = (FileListDiff) obj;
        return this.added.equals(other.added)
            && this.removed.equals(other.removed)
            && this.altered.equals(other.altered)
            && this.unaltered.equals(other.unaltered);
    }

}