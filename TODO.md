# Things that need adding/fixing

- The JGroups and Hazelcast transports don't work
- All the //TODO Messages refs need an actual message made for them in
  the Messages bundle.
- Add a JobLoader than uses ServiceLoader to pick up job's declared in
  code (i.e. should work for both the fluent and groovy jobs).
- Coherence repo is broken
- Lots of things need tests written for them
- Change the way workers are located
- Glassfish isn't passing the TCK TransactionTests when using a managed
  thread factory.
