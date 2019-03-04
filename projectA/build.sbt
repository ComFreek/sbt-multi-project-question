unmanagedBase := baseDirectory.value / ".." / "projectB" / "deploy" / "lib"

lazy val projectB = RootProject(file("../projectB/src/project"))

lazy val projectA = Project(id = "projectA", base = file(".")).settings(
  name := "projectA",
  version := "0.1",
  scalaVersion := "2.12.8",
  scalacOptions in ThisBuild ++= Seq("-unchecked", "-deprecation")
).dependsOn(projectB)
