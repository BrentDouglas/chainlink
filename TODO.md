# Things that need adding/fixing

- Make JGroups, Coherence and Infinispan transport work
- Add a Shoal transport for Glassfish
- All the //TODO Messages refs need an actual message made for them in
  the Messages bundle.
- Add a JobLoader than uses ServiceLoader to pick up job's declared in
  code (i.e. should work for both the fluent and groovy jobs).
- Lots of things need tests written for them
- Glassfish isn't passing the TCK TransactionTests when using a managed
  thread factory.
- Hook progress up
- Look at thread safety in extensions
- Find out why using MariaDB is so slow
