import asyncio, sys
buffer=[]
countAdd=0;
countDel=0;

async def writeInput(countAdd):
	while True:
		print("PUT start " + str(countAdd))		
		countAdd+=1
		buffer.append(1)
		print(buffer)		
		await asyncio.sleep(1)
		print("PUT after sleep " + str(countAdd))


async def deleteInput(countDel):
	while True:
		print("DEL start " + str(countDel))		
		print(buffer)		
		countDel+=1
		# if two statements are reversed this breaks
		await asyncio.sleep(1)
		print("DEL after sleep " + str(countDel))		
		buffer.pop()
		print(buffer)		
		
async def chainCoroutine():
	counter =0 
	print(buffer)
	counter+=1
	await writeInput(counter)
	print(buffer)
	await deleteInput(counter)
	# syncwriteInput(counter)


loop = asyncio.get_event_loop()
# RUNNING OK arra always empty
# loop.run_until_complete((asyncio.gather(
#     writeInput(0),
#     deleteInput(0)
# )))
loop.run_until_complete((asyncio.gather(
    writeInput(0),
    deleteInput(0)
)))
