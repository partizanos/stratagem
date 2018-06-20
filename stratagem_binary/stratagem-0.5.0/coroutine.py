import asyncio, sys
buffer=[]
countAdd=0;
countDel=0;

async def readInput(countAdd):
	while True:
		print(buffer)

		countAdd+=1
		buffer.append(1)
		print("PUT" + str(countAdd))
		await asyncio.sleep(1)


def syncreadInput():
	print("PUT" + str(countAdd))
	buffer.append(1)




async def deleteInput(countDel):
	while True:
		print(buffer)
		countDel+=1
		await asyncio.sleep(1)
		print("DEL" + str(countDel))
		buffer.pop()


async def printInput():
	counter =0 
	print(buffer)
	counter+=1
	await readInput(counter)
	print(buffer)
	await deleteInput(counter)
	# syncreadInput(counter)


loop = asyncio.get_event_loop()
loop.run_until_complete((asyncio.gather(
    readInput(0),
    deleteInput(0)
)))
