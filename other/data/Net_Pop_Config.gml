popconfig [
  USER "flandar"
  PASS "dragonegg"
  HOST "pop.mail.yahoo.com"
  PORT "110"
  ACTIVITY "java -cp c:/aaron/ aj.nf.Main ProcessOrders"
  REQUIRETEXT "@LOGIN"
  DELETEOKAY false
  notes "ACTIVITY no activity = file dump"
  notes "ACTIVITY execute activity"
  notes "ACTIVITY forward with e-mail address (ie @ in line)"
  notes "REQUIRETEXT '' for all"
]
