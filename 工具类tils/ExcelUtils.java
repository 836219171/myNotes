import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.DateUtil;
import org.apache.poi.ss.usermodel.Font;
import org.apache.poi.ss.usermodel.IndexedColors;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.usermodel.WorkbookFactory;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import com.atguigu.bean.ExcelBean2;
import com.atguigu.service.SpuServiceInf;

/**
 * @author BY QinLiang Time:2018年5月11日
 */

public class ExcelUtils {
    
	/** 读取excel,在控制台显示 */
	public static <E> List<E> readExcel(String fileName, String ClassName) {
		List<E> list = new ArrayList<E>();
		try {
			Class<?> c1 = null;

			c1 = Class.forName(ClassName);

			String filePath = fileName;
			FileInputStream is = null;
			SimpleDateFormat fmt = new SimpleDateFormat("yyyy-MM-dd");

			is = new FileInputStream(filePath);

			// 获得工作簿
			Workbook workbook = null;

			workbook = WorkbookFactory.create(is);

			// 获得工作表个数
			int sheetCount = workbook.getNumberOfSheets();
			// 遍历工作表
			for (int i = 0; i < sheetCount; i++) {
				Sheet sheet = workbook.getSheetAt(i);
				// 获得行数
				int rows = sheet.getLastRowNum() + 1;
				// 获得列数，先获得一行，在得到该行列数
				Row tmp = sheet.getRow(0);

				// 取得一行中有效的单元格数
				int cols = tmp.getPhysicalNumberOfCells();
				// 一行代表一个对象，一列代表一个对象的属性值
				for (int row = 0; row < rows; row++) {
					// 有多少行就创建多少个对象
					Object object = null;

					// 实例化一个对象
					object = c1.newInstance();

					// fiels属性组，循环获取属性
					Field[] fields = c1.getDeclaredFields();

					Row r = sheet.getRow(row);

					for (int col = 0; col < cols; col++) {

						// fieldId 为bean中的第几个属性，等于col
						int fieldId = col;
						// 可以通过反射设置私有属性
						fields[fieldId].setAccessible(true);
						Cell cell = r.getCell(col);
						int cellType = cell.getCellType();
						// System.out.println("cellType=" + cellType+"
						// col="+col);
						String cellValue = null;
						// 根据cellType的类型确定用什么对象类型来接收
						switch (cellType) {
						case Cell.CELL_TYPE_NUMERIC:
							if (DateUtil.isCellDateFormatted(cell)) {
								// 将日期格式化为字符串性
								cellValue = fmt.format(cell.getDateCellValue());
								// 将字符串型转为date型并存到对象里

								fields[fieldId].set(object, new java.sql.Date(
										new SimpleDateFormat("yyyy-MM-dd").parse(cellValue).getTime()));

								System.out.print("date cellValue=" + cellValue + "  ");
							} else {
								// 数字
								cellValue = String.valueOf(cell.getNumericCellValue()); 
								if (null != cellValue) {
									//整型的处理
									if (IsInt(cellValue)) {
										cellValue = cellValue.substring(0, cellValue.lastIndexOf("."));
										fields[fieldId].set(object, Integer.valueOf(cellValue));
										System.out.print("int  cellValue=" + cellValue + "  ");
									} else {
										// float类型处理
										fields[fieldId].set(object, Double.valueOf(cellValue));
										System.out.print("double cellValue=" + cellValue + "  ");
									}
								} else {
									throw new IllegalArgumentException("此单元格为空");
								}
							}
							break;

						case Cell.CELL_TYPE_STRING:
							cellValue = cell.getStringCellValue();
							fields[fieldId].set(object, cellValue);
							System.out.print("string cellValue=" + cellValue + "  ");
							break;

						case Cell.CELL_TYPE_FORMULA:
							cell.setCellType(Cell.CELL_TYPE_STRING);
							cellValue = cell.getStringCellValue();
							fields[fieldId].set(object, cellValue);
							System.out.print("cellValue cellValue=" + cellValue + "  ");

							break;

						case Cell.CELL_TYPE_BLANK:
							cell.setCellType(Cell.CELL_TYPE_STRING);
							cellValue = cell.getStringCellValue();
							fields[fieldId].set(object, cellValue);
							System.out.print("cellValue cellValue=" + cellValue + "  ");

							break;

						case Cell.CELL_TYPE_BOOLEAN:
							cellValue = String.valueOf(cell.getBooleanCellValue());
							fields[fieldId].set(object, Boolean.valueOf(cellValue));
							System.out.print("boolean cellValue=" + cellValue + "  ");

							break;

						case Cell.CELL_TYPE_ERROR:
							throw new IllegalArgumentException("错误");

						default:
							break;
						}

					}
					System.out.println();
					list.add((E) object);
				}

			}

		} catch (Exception e) {
			e.printStackTrace();

		}

		return list;

	}
      
	/** 创建excel，需要手动输入值 */
	public static Workbook createWorkBook(List<Map<String, Object>> list, String[] keys, String columnNames[]) {
		// 创建excel工作簿
		Workbook wb = new HSSFWorkbook();
		// 创建第一个sheet（页），并命名
		Sheet sheet = wb.createSheet(list.get(0).get("sheetName").toString());
		// 手动设置列宽。第一个参数表示要为第几列设；，第二个参数表示列的宽度，n为列高的像素数。
		for (int i = 0; i < keys.length; i++) {
			sheet.setColumnWidth((short) i, (short) (35.7 * 150));
		}

		// 创建第一行
		Row row = sheet.createRow((short) 0);

		// 创建两种单元格格式
		CellStyle cs = wb.createCellStyle();
		CellStyle cs2 = wb.createCellStyle();

		// 创建两种字体
		Font f = wb.createFont();
		Font f2 = wb.createFont();

		// 创建第一种字体样式（用于列名）
		f.setFontHeightInPoints((short) 10);
		f.setColor(IndexedColors.BLACK.getIndex());
		f.setBoldweight(Font.BOLDWEIGHT_BOLD);

		// 创建第二种字体样式（用于值）
		f2.setFontHeightInPoints((short) 10);
		f2.setColor(IndexedColors.BLACK.getIndex());

		// 设置第一种单元格的样式（用于列名）
		cs.setFont(f);
		cs.setBorderLeft(CellStyle.BORDER_THIN);
		cs.setBorderRight(CellStyle.BORDER_THIN);
		cs.setBorderTop(CellStyle.BORDER_THIN);
		cs.setBorderBottom(CellStyle.BORDER_THIN);
		cs.setAlignment(CellStyle.ALIGN_CENTER);

		// 设置第二种单元格的样式（用于值）
		cs2.setFont(f2);
		cs2.setBorderLeft(CellStyle.BORDER_THIN);
		cs2.setBorderRight(CellStyle.BORDER_THIN);
		cs2.setBorderTop(CellStyle.BORDER_THIN);
		cs2.setBorderBottom(CellStyle.BORDER_THIN);
		cs2.setAlignment(CellStyle.ALIGN_CENTER);
		// 设置列名
		for (int i = 0; i < columnNames.length; i++) {
			Cell cell = row.createCell(i);
			cell.setCellValue(columnNames[i]);
			cell.setCellStyle(cs);
		}
		// 设置每行每列的值
		for (short i = 1; i < list.size(); i++) {
			// Row 行,Cell 方格 , Row 和 Cell 都是从0开始计数的
			// 创建一行，在页sheet上
			Row row1 = sheet.createRow((short) i);
			// 在row行上创建一个方格
			for (short j = 0; j < keys.length; j++) {
				Cell cell = row1.createCell(j);
				//为每个单元格赋值
				cell.setCellValue(list.get(i).get(keys[j]) == null ? " " : list.get(i).get(keys[j]).toString());
				cell.setCellStyle(cs2);
			}
		}
		return wb;
	} 


	
	
	/** 从数据库导出表，对 createWorkBook的进一步封装,实际导出路径为fileName+时间戳,
	 * list为从导出的javaBean的集合*/
	public  static <E> void outExcel(String[] columnNames,List<E> list,String fileName)  {
		
		try {
			if (list != null && list.size() >0) {
			 	Class<?> c1 = list.get(0).getClass();
				Object object = c1.newInstance();
				Field[] fields = c1.getDeclaredFields();
				String[] keys = new String[fields.length];
				List<Map<String, Object>> listMap = new ArrayList<>();
				//给工作表命名,map1为第一张工作表
				Map<String, Object> map1 = new HashMap<String, Object>();  
				map1.put("sheetName", "wind_data1");
				listMap.add(map1);
				 for (int i = 0; i < list.size(); i++) {
					 Map<String, Object> map = new HashMap<String, Object>(); 
					 for (int j = 0; j < fields.length; j++) {
						 fields[j].setAccessible(true);
						map.put(fields[j].toString(),contactGet(list.get(i),fields[j].getName().toString()) );
						//属性名为map的键值
						 keys[j] = fields[j].toString();
						
					}
					
					 listMap.add(map);
				}
				 
					Workbook wb = createWorkBook(listMap, keys, columnNames);
					long curTime = System.currentTimeMillis();
				    File file = new File(fileName+curTime+".xlsx");  
			        OutputStream os = new FileOutputStream(file);  
			        os.flush();  
			        wb.write(os);  
			        os.close(); 
			        System.out.println("finish");
			}
	
		} catch (Exception e) {
			e.printStackTrace();
		} 

	}
	
	   /** 反射执行属性的get方法，得到返回值 */     
	private static  Object  contactGet(Object object,String field){
		Class clazz = object.getClass();  
		Object result = null;
		 Method[] methods  =clazz.getMethods();
 		String o = field.substring(0, 1).toUpperCase()+field.substring(1);
		//
		String getMethod = "get"+o;
		System.out.println("getMethod="+getMethod);
		for (Method method : methods) {
			
			System.out.println("method="+method.getName());
			if (method.getName().equals(getMethod)) {
				try {
					//result = method.invoke(object);
					System.out.println("**********method="+method);
					result = method.invoke(object);
					System.out.println("***************"+result);
				} catch (IllegalAccessException e) {
					e.printStackTrace();
				} catch (IllegalArgumentException e) {
					e.printStackTrace();
				} catch (InvocationTargetException e) {
					e.printStackTrace();
				}
			}
		}
		return  result;
 
		
	}
	   /** 判断是否是整型 */
	private static boolean IsInt(String num) {

		String preNum = num.substring(num.lastIndexOf("."));
		int isLength = preNum.length();
		if (isLength != 0) {
			if (isLength == 2 && ".0".equals(preNum)) {
				return true;
			}

		}
		return false;

	}
	

}












