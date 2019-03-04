Scenario:

- I want to develop a projectA written in Scala, which depends on projectB, also written in Scala.
- It will often be the case that I need to modify projectB as well. Hence, I will have a local Git clone of projectB (as in [my repository](https://github.com/ComFreek/sbt-multi-project-question) as a submodule).
- Now projectA should pull the dependency on projectB directly from that cloned Git repository of projectB.

In `projectA/build.sbt` tried:

```scala
unmanagedBase := baseDirectory.value / ".." / "projectB" / "deploy" / "lib"

lazy val projectB = RootProject(file("../projectB/src/project"))

lazy val projectA = Project(id = "projectA", base = file(".")).settings(
  name := "projectA",
  version := "0.1",
  scalaVersion := "2.12.8",
  scalacOptions in ThisBuild ++= Seq("-unchecked", "-deprecation")
).dependsOn(projectB)
```

However, it seems that [`projectB/src/build.sbt`](https://github.com/UniFormal/MMT/blob/b558245d2ea927ceb699edfdee7a18d51fe96955/src/build.sbt#L118) uses unmanaged libraries put into [`projectB/deploy/lib`](https://github.com/UniFormal/MMT/tree/b558245d2ea927ceb699edfdee7a18d51fe96955/deploy/lib) which cannot be found when `sbt compile` is run from within the scope of projectA - even with the `unmanagedBase` property set.

Concretely, you can reproduce it as follows:

1. Open an SBT shell in `projectA`
2. Run `compile` and get
    ```
    [IJ]sbt:projectA> compile
    [info] Compiling 13 Scala sources to ...\sbt-multi-project-question\projectB\src\project\target\scala-2.12\classes ...
    [error] ...\sbt-multi-project-question\projectB\src\project\File.scala:19:11: object tools is not a member of package scala
    [error]     scala.tools.nsc.io.File(f.toString).appendAll(strings:_*)
    [error]           ^
    [error] ...\sbt-multi-project-question\projectB\src\project\Utils.scala:31:14: object Keys is not a member of package sbt
    [error]   import sbt.Keys.packageBin
    [error]              ^
    [error] ...\sbt-multi-project-question\projectB\src\project\Utils.scala:33:36: not found: value Def
    [error]   def deployPackage(name: String): Def.Initialize[Task[Unit]] =
    [error]                                    ^
    [error] ...\sbt-multi-project-question\projectB\src\project\Utils.scala:34:5: not found: value packageBin
    [error]     packageBin in Compile map {jar => deployTo(Utils.deploy / name)(jar)}
    [error]     ^
    [error] ...\sbt-multi-project-question\projectB\src\project\Utils.scala:34:19: not found: value Compile
    [error]     packageBin in Compile map {jar => deployTo(Utils.deploy / name)(jar)}
    [error]                   ^
    [error] ...\sbt-multi-project-question\projectB\src\project\Utils.scala:45:39: type File is not a member of package sbt
    [error]   def deployTo(target: File)(jar: sbt.File): Unit = {
    [error]                                       ^
    [error] ...\sbt-multi-project-question\projectB\src\project\Utils.scala:39:36: not found: value Def
    [error]   def deployMathHub(target: File): Def.Initialize[Task[Unit]] =
    [error]                                    ^
    [error] ...\sbt-multi-project-question\projectB\src\project\Utils.scala:40:5: not found: value packageBin
    [error]     packageBin in Compile map {jar => deployTo(target)(jar)}
    [error]     ^
    [error] ...\sbt-multi-project-question\projectB\src\project\Utils.scala:40:19: not found: value Compile
    [error]     packageBin in Compile map {jar => deployTo(target)(jar)}
    [error]                   ^
    [error] ...\sbt-multi-project-question\projectB\src\project\Utils.scala:123:25: not found: type Logger
    [error]   def delRecursive(log: Logger, path: File): Unit = {
    [error]                         ^
    [error] ...\sbt-multi-project-question\projectB\src\project\VersionSpecificProject.scala:8:44: not found: type Project
    [error] case class VersionSpecificProject(project: Project, excludes: Exclusions) {
    [error]                                            ^
    [error] ...\sbt-multi-project-question\projectB\src\project\VersionSpecificProject.scala:13:48: not found: type Project
    [error]   def aggregate(projects: ProjectReference*) : Project = project.aggregate(excludes(projects.toList) :_*)
    [error]                                                ^
    [error] ...\sbt-multi-project-question\projectB\src\project\VersionSpecificProject.scala:13:27: not found: type ProjectReference
    [error]   def aggregate(projects: ProjectReference*) : Project = project.aggregate(excludes(projects.toList) :_*)
    [error]                           ^
    [error] ...\sbt-multi-project-question\projectB\src\project\VersionSpecificProject.scala:17:48: not found: type Project
    [error]   def dependsOn(projects: ProjectReference*) : Project = {
    [error]                                                ^
    [error] ...\sbt-multi-project-question\projectB\src\project\VersionSpecificProject.scala:17:27: not found: type ProjectReference
    [error]   def dependsOn(projects: ProjectReference*) : Project = {
    [error]                           ^
    [error] ...\sbt-multi-project-question\projectB\src\project\VersionSpecificProject.scala:18:47: not found: type ClasspathDep
    [error]     def toClassPathDep(p: ProjectReference) : ClasspathDep[ProjectReference] = p
    [error]                                               ^
    [error] ...\sbt-multi-project-question\projectB\src\project\VersionSpecificProject.scala:18:27: not found: type ProjectReference
    [error]     def toClassPathDep(p: ProjectReference) : ClasspathDep[ProjectReference] = p
    [error]                           ^
    [error] ...\sbt-multi-project-question\projectB\src\project\VersionSpecificProject.scala:23:59: not found: type Project
    [error]   def aggregatesAndDepends(projects: ProjectReference*) : Project = {
    [error]                                                           ^
    [error] ...\sbt-multi-project-question\projectB\src\project\VersionSpecificProject.scala:23:38: not found: type ProjectReference
    [error]   def aggregatesAndDepends(projects: ProjectReference*) : Project = {
    [error]                                      ^
    [error] ...\sbt-multi-project-question\projectB\src\project\VersionSpecificProject.scala:24:47: not found: type ClasspathDep
    [error]     def toClassPathDep(p: ProjectReference) : ClasspathDep[ProjectReference] = p
    [error]                                               ^
    [error] ...\sbt-multi-project-question\projectB\src\project\VersionSpecificProject.scala:24:27: not found: type ProjectReference
    [error]     def toClassPathDep(p: ProjectReference) : ClasspathDep[ProjectReference] = p
    [error]                           ^
    [error] ...\sbt-multi-project-question\projectB\src\project\VersionSpecificProject.scala:30:37: not found: type Project
    [error]   implicit def fromProject(project: Project) : VersionSpecificProject = VersionSpecificProject(project, Exclusions())
    [error]                                     ^
    [error] ...\sbt-multi-project-question\projectB\src\project\VersionSpecificProject.scala:34:28: not found: type ProjectReference
    [error] case class Exclusions(lst: ProjectReference*) {
    [error]                            ^
    [error] ...\sbt-multi-project-question\projectB\src\project\VersionSpecificProject.scala:31:61: not found: type Project
    [error]   implicit def toProject(vProject: VersionSpecificProject): Project = vProject.project
    [error]                                                             ^
    [error] ...\sbt-multi-project-question\projectB\src\project\VersionSpecificProject.scala:35:68: not found: type ProjectReference
    [error]   private def javaVersion(versions: List[String], exclusions: List[ProjectReference]) : Exclusions = {
    [error]                                                                    ^
    [error] ...\sbt-multi-project-question\projectB\src\project\VersionSpecificProject.scala:43:22: not found: type ProjectReference
    [error]   def :::(lst2: List[ProjectReference]) = Exclusions(lst.toList ::: lst2 : _*)
    [error]                      ^
    [error] ...\sbt-multi-project-question\projectB\src\project\VersionSpecificProject.scala:39:25: not found: type ProjectReference
    [error]   def java7(exclusions: ProjectReference*): Exclusions = javaVersion(List("1.7", "7"), exclusions.toList)
    [error]                         ^
    [error] ...\sbt-multi-project-question\projectB\src\project\VersionSpecificProject.scala:40:25: not found: type ProjectReference
    [error]   def java8(exclusions: ProjectReference*): Exclusions = javaVersion(List("1.8", "8"), exclusions.toList)
    [error]                         ^
    [error] ...\sbt-multi-project-question\projectB\src\project\VersionSpecificProject.scala:41:25: not found: type ProjectReference
    [error]   def java9(exclusions: ProjectReference*): Exclusions = javaVersion(List("1.9", "9"), exclusions.toList)
    [error]                         ^
    [error] ...\sbt-multi-project-question\projectB\src\project\VersionSpecificProject.scala:46:18: not found: value ScopeFilter
    [error]   def toFilter : ScopeFilter.ProjectFilter = {
    [error]                  ^
    [error] ...\sbt-multi-project-question\projectB\src\project\VersionSpecificProject.scala:47:5: not found: value inAnyProject
    [error]     inAnyProject -- inProjects(lst :_*)
    [error]     ^
    [error] ...\sbt-multi-project-question\projectB\src\project\VersionSpecificProject.scala:47:21: not found: value inProjects
    [error]     inAnyProject -- inProjects(lst :_*)
    [error]                     ^
    [error] ...\sbt-multi-project-question\projectB\src\project\VersionSpecificProject.scala:51:28: not found: type ProjectReference
    [error]   private def equals(left: ProjectReference, right: ProjectReference) : Boolean = {
    [error]                            ^
    [error] ...\sbt-multi-project-question\projectB\src\project\VersionSpecificProject.scala:51:53: not found: type ProjectReference
    [error]   private def equals(left: ProjectReference, right: ProjectReference) : Boolean = {
    [error]                                                     ^
    [error] ...\sbt-multi-project-question\projectB\src\project\VersionSpecificProject.scala:61:25: not found: type ProjectReference
    [error]   def excludes(project: ProjectReference) : Boolean = lst.exists(equals(_, project))
    [error]                         ^
    [error] ...\sbt-multi-project-question\projectB\src\project\VersionSpecificProject.scala:62:54: not found: type ProjectReference
    [error]   def apply(projects: List[ProjectReference]) : List[ProjectReference] = projects.filterNot(this.excludes)
    [error]                                                      ^
    [error] ...\sbt-multi-project-question\projectB\src\project\VersionSpecificProject.scala:62:28: not found: type ProjectReference
    [error]   def apply(projects: List[ProjectReference]) : List[ProjectReference] = projects.filterNot(this.excludes)
    [error]                            ^
    [error] ...\sbt-multi-project-question\projectB\src\project\VersionSpecificProject.scala:64:17: not found: type ProjectReference
    [error]   def map[B](f: ProjectReference => B) : Seq[B] = lst.map(f)
    [error]                 ^
    [error] ...\sbt-multi-project-question\projectB\src\project\VersionSpecificProject.scala:65:21: not found: type ProjectReference
    [error]   def foreach[U](f: ProjectReference => U) : Exclusions = {lst.foreach[U](f); this }
    [error]                     ^
    [error] 39 errors found
    [error] (ProjectRef(uri("file:/.../sbt-multi-project-question/projectB/src/project/"), "project") / Compile / compileIncremental) Compilation failed
    [error] Total time: 5 s, completed 04.03.2019, 10:10:34
    [IJ]sbt:projectA>
    ```
	
However, the following works:

1. Open an SBT shell in `projectB/src`.
2. Run `compile` (beware that it takes ~12 minutes on my machine!)

