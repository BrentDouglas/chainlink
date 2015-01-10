# Things that need adding/fixing

- The Glassfish TCK won't run with the CDI injector. It needs OSGI
  dependencies on the stuff in the inject-cdi module. Ideally this can
  be added at deployment time.
- The JGroups and Hazelcast transports don't work
- All the //TODO Messages refs need an actual message made for them in
  the Messages bundle.
- Add a JobLoader than uses ServiceLoader to pick up job's declared in
  code (i.e. should work for both the fluent and groovy jobs).
- Hazelcast, Gridgain, Coherence and MongoDB repos are broken
- CdiArtifactLoaderFactory starts weld so is only usable in SE mode
- Lots of things need tests written for them
- Change the way workers are located
